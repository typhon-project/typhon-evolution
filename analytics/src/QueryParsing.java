
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.rascalmpl.debug.IRascalMonitor;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.JavaToRascal;
import org.rascalmpl.interpreter.NullRascalMonitor;
import org.rascalmpl.interpreter.env.GlobalEnvironment;
import org.rascalmpl.interpreter.env.ModuleEnvironment;
import org.rascalmpl.interpreter.load.IRascalSearchPathContributor;
import org.rascalmpl.interpreter.load.StandardLibraryContributor;
import org.rascalmpl.interpreter.load.URIContributor;
import org.rascalmpl.interpreter.utils.RascalManifest;
import org.rascalmpl.library.util.PathConfig;
import org.rascalmpl.uri.URIResolverRegistry;
import org.rascalmpl.uri.URIUtil;
import org.rascalmpl.uri.classloaders.SourceLocationClassLoader;
import org.rascalmpl.uri.libraries.ClassResourceInput;
import org.rascalmpl.util.ConcurrentSoftReferenceObjectPool;
import org.rascalmpl.values.ValueFactoryFactory;

import io.usethesource.vallang.IList;
import io.usethesource.vallang.IMap;
import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IString;
import io.usethesource.vallang.ITuple;
import io.usethesource.vallang.IValue;
import io.usethesource.vallang.IValueFactory;
import io.usethesource.vallang.type.TypeFactory;
import model.TyphonModel;
import typhonml.Attribute;
import typhonml.Entity;
import typhonml.Model;
import typhonml.Relation;
import typhonml.impl.ModelImpl;

public class QueryParsing {
	private static Evaluator evaluator = null;
	private static IValueFactory vf = null;

	static Logger logger = Logger.getLogger(QueryParsing.class);

	static {
		PropertyConfigurator.configure(
				System.getProperty("user.dir") + File.separator + "resources" + File.separator + "log4j.properties");
	}

	private static boolean init() {

		try {
			ISourceLocation root = URIUtil.createFileLocation(
					QueryParsing.class.getProtectionDomain().getCodeSource().getLocation().getPath());

			PathConfig pcfg = PathConfig.fromSourceProjectRascalManifest(root);

			GlobalEnvironment heap = new GlobalEnvironment();
			evaluator = new Evaluator(ValueFactoryFactory.getValueFactory(), new PrintWriter(System.err, true),
					new PrintWriter(System.out, false), new ModuleEnvironment("$typhonql$", heap), heap);

			evaluator.addRascalSearchPathContributor(StandardLibraryContributor.getInstance());

			for (IValue path : pcfg.getJavaCompilerPath()) {
				ISourceLocation loc = (ISourceLocation) path;

				if (loc.getPath().endsWith(".jar")) {
					loc = URIUtil.changePath(URIUtil.changeScheme(loc, "jar+" + loc.getScheme()), loc.getPath() + "!/");
				}

				evaluator.addRascalSearchPath(loc);
			}

			logger.info("Importing plugin ...");
			evaluator.doImport(null, "TyphonQLAnalytics");
			vf = ValueFactoryFactory.getValueFactory();
			logger.info("Plugin imported with success");
			return true;
		} catch (Exception | Error e) {
			logger.error("Impossible to initialize the QueryParsing plugin\nCause:\n" + e.getCause());
			return false;
		}
	}

	public static void main(String[] args) throws URISyntaxException, IOException {
		if (!init()) {
			logger.error("Query Parsing plugin exit");
			return;
		}

		Scanner in = new Scanner(System.in);
		while (true) {
			String query = in.nextLine();

			try {
				eval(query);
			} catch (Exception | Error e) {
				logger.error("Problem when analyzing: " + query);
				e.printStackTrace();
			}
		}

	}

	static void analyzeAtttributeSelector(AttributeSelector sel, TyphonModel model) {
		AttributeSelector as = sel;
		List<Join> joins = new ArrayList<Join>();
		Entity entity = model.getEntityTypeFromName(sel.getEntityName());
		for (String attrRel : sel.getAttributes()) {
			Relation relation = model.getRelationFromNameInEntity(attrRel, entity);
			if (relation != null) {
				List<String> attributes1 = new ArrayList<String>();
				attributes1.add(attrRel);

				joins.add(
						new Join(entity.getName(), attributes1, relation.getType().getName(), new ArrayList<String>()));

				entity = relation.getType();
			} else {
				Attribute attribute = model.getAttributeFromNameInEntity(attrRel, entity);
				if (attribute != null) {
					List<String> attributes = new ArrayList<String>();
					attributes.add(attrRel);
					as = new AttributeSelector(entity.getName(), attributes);
				} else {
					logger.error("Query is obsolete: " + attrRel + " in " + sel.getEntityName());
					return;
				}
			}
		}

		if (joins.size() > 0) {
			sel.setImplicitJoins(joins);
			sel.setImplicitSel(as);
		}

	}

	public static Query eval(String query) {
		logger.info("Query analyzing ...\n" + query);
		IValue v = evaluator.getEvaluator().call("parseQuery", vf.string(query));
		Query q = parseResult(v);
		logger.info("Query analyzed");
		q.print();
		return q;
	}

	private static Query parseResult(IValue v) {
		Query res = new Query();

		ITuple tuple = (ITuple) v;

		ITuple queryMetadata = (ITuple) tuple.get(0);
		String originalQuery = ((IString) queryMetadata.get(0)).getValue();
		String normalizedQuery = ((IString) queryMetadata.get(1)).getValue();
		String displayableQuery = ((IString) queryMetadata.get(2)).getValue();
		String queryType = ((IString) queryMetadata.get(3)).getValue();

		res.setOriginalQuery(originalQuery);
		res.setNormalizedQuery(normalizedQuery);
		res.setDisplayableQuery(displayableQuery);
		res.setQueryType(queryType);

		ITuple queryData = (ITuple) tuple.get(1);
		List<String> mainEntities = getList(queryData.get(0));
		res.setMainEntities(mainEntities);

		Iterator<IValue> joinIterator = ((IList) queryData.get(1)).iterator();
		while (joinIterator.hasNext()) {
			ITuple join = (ITuple) joinIterator.next();
			String entityName1 = ((IString) join.get(0)).getValue();
			List<String> attrs1 = getList(join.get(1));
			String entityName2 = ((IString) join.get(2)).getValue();
			List<String> attrs2 = getList(join.get(3));
			Join j = new Join(entityName1, attrs1, entityName2, attrs2);
			res.getJoins().add(j);
		}

		Iterator<IValue> attrSelectorIterator = ((IList) queryData.get(2)).iterator();
		while (attrSelectorIterator.hasNext()) {
			ITuple comp = (ITuple) attrSelectorIterator.next();
			String entityName = ((IString) comp.get(0)).getValue();
			List<String> attrs = getList(comp.get(1));
			AttributeSelector ac = new AttributeSelector(entityName, attrs);
			res.addAttributeSelector(ac);
		}

		IList insertList = ((IList) queryData.get(3));
		res.setInserts(getInserts(insertList));

		return res;
	}

	private static Insert getInsert(ITuple ins) {
		String entityName = ((IString) ins.get(0)).getValue();
		IList children = (IList) ins.get(1);
		List<Insert> sub = getInserts(children);

		return new Insert(entityName, sub);
	}

	private static List<Insert> getInserts(IList children) {
		List<Insert> res = new ArrayList<Insert>();
		Iterator<IValue> it = children.iterator();
		while (it.hasNext()) {
			IValue v = it.next();
			res.add(getInsert((ITuple) v));
		}
		return res;
	}

	private static List<String> getList(IValue v) {
		return getList((IList) v);
	}

	private static List<String> getList(IList list) {
		List<String> res = new ArrayList<String>();
		Iterator<IValue> it = list.iterator();
		while (it.hasNext())
			res.add(((IString) it.next()).getValue());
		return res;
	}

	public static void analyzeAttributeSelector(AttributeSelector sel) {

	}

}

class Query {
	private TyphonModel model = null;

	private String originalQuery;
	private String normalizedQuery;
	private String displayableQuery;
	private String queryType;

	private List<String> mainEntities = new ArrayList<String>();

	private List<Join> joins = new ArrayList<Join>();
	private List<AttributeSelector> attributeSelectors = new ArrayList<AttributeSelector>();
	private List<Insert> inserts = new ArrayList<Insert>();

	private TyphonModel getModel() {
		if (model == null) {
			model = TyphonModel.getCurrentModel();
		}

		return model;
	}

	public void print() {
		QueryParsing.logger.debug("Original query: " + originalQuery);
		QueryParsing.logger.debug("Normalized query: " + normalizedQuery);
		QueryParsing.logger.debug("Displayable query: " + displayableQuery);
		QueryParsing.logger.debug("Query type: " + queryType);
		QueryParsing.logger.debug("*****************************************");
		QueryParsing.logger.debug("Main entities: " + mainEntities);
		QueryParsing.logger.debug("*****************************************");
		if (joins.size() > 0) {
			QueryParsing.logger.debug("Joins between entities: ");
			for (Join j : joins) {
				QueryParsing.logger.debug("   " + j.getEntityName1() + j.getAttributes1() + " AND " + j.getEntityName2()
						+ j.getAttributes2());
			}

			QueryParsing.logger.debug("*****************************************");
		}

		if (attributeSelectors.size() > 0) {
			QueryParsing.logger.debug("Attribute selectors:");
			for (AttributeSelector c : attributeSelectors) {
				QueryParsing.logger.debug("   " + c.getEntityName() + c.getAttributes()
						+ (c.containsImplicitJoins() ? " contains implicit joins:" : ""));
				if (c.containsImplicitJoins()) {
					for(Join j : c.getImplicitJoins()) 
						QueryParsing.logger.debug("   -> " + j.getEntityName1() + j.getAttributes1() + " AND " + j.getEntityName2()
						+ j.getAttributes2());
					QueryParsing.logger.debug("   -> " + c.getImplicitSel().getEntityName() + c.getImplicitSel().getAttributes());
				}
			}
			QueryParsing.logger.debug("*****************************************");
		}

		if (inserts.size() > 0) {
			QueryParsing.logger.debug("Inserts: " + inserts);
		}

	}

	public String getOriginalQuery() {
		return originalQuery;
	}

	public void setOriginalQuery(String originalQuery) {
		this.originalQuery = originalQuery;
	}

	public String getNormalizedQuery() {
		return normalizedQuery;
	}

	public void setNormalizedQuery(String normalizedQuery) {
		this.normalizedQuery = normalizedQuery;
	}

	public String getDisplayableQuery() {
		return displayableQuery;
	}

	public void setDisplayableQuery(String displayableQuery) {
		this.displayableQuery = displayableQuery;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public List<Join> getJoins() {
		return joins;
	}

	public void setJoins(List<Join> joins) {
		this.joins = joins;
	}

	public void addAttributeSelector(AttributeSelector sel) {
		QueryParsing.analyzeAtttributeSelector(sel, getModel());
		attributeSelectors.add(sel);
	}

	public List<AttributeSelector> getAttributeSelectors() {
		return attributeSelectors;
	}

	public void setAttributeSelectors(List<AttributeSelector> attributeSelectors) {
		this.attributeSelectors = attributeSelectors;
	}

	public List<Insert> getInserts() {
		return inserts;
	}

	public void setInserts(List<Insert> inserts) {
		this.inserts = inserts;
	}

	public List<String> getMainEntities() {
		return mainEntities;
	}

	public void setMainEntities(List<String> mainEntities) {
		this.mainEntities = mainEntities;
	}
}

class AttributeSelector {
	private String entityName;
	private List<String> attributes;

	private List<Join> implicitJoins;
	private AttributeSelector implicitSel;

	public AttributeSelector(String entityName, List<String> attributes) {
		this.entityName = entityName;
		this.attributes = attributes;
	}

	public boolean containsImplicitJoins() {
		return implicitJoins.size() > 0;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public List<Join> getImplicitJoins() {
		return implicitJoins;
	}

	public void setImplicitJoins(List<Join> implicitJoins) {
		this.implicitJoins = implicitJoins;
	}

	public AttributeSelector getImplicitSel() {
		return implicitSel;
	}

	public void setImplicitSel(AttributeSelector implicitSel) {
		this.implicitSel = implicitSel;
	}

}

class Insert {
	private String entityName;
	private List<Insert> children = new ArrayList<Insert>();

	public Insert(String entityName, List<Insert> children) {
		setEntityName(entityName);
		setChildren(children);
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public List<Insert> getChildren() {
		return children;
	}

	public void setChildren(List<Insert> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		String res = entityName + "{";
		for (int i = 0; i < children.size(); i++) {
			if (i > 0)
				res += ", ";

			Insert child = children.get(i);
			res += child;
		}
		res += "}";
		return res;
	}

}

class Join {
	private String entityName1;
	private List<String> attributes1;
	private String entityName2;
	private List<String> attributes2;

	public Join(String entityName1, List<String> attributes1, String entityName2, List<String> attributes2) {
		setEntityName1(entityName1);
		setEntityName2(entityName2);
		setAttributes1(attributes1);
		setAttributes2(attributes2);
	}

	public String getEntityName1() {
		return entityName1;
	}

	public void setEntityName1(String entityName1) {
		this.entityName1 = entityName1;
	}

	public List<String> getAttributes1() {
		return attributes1;
	}

	public void setAttributes1(List<String> attributes1) {
		this.attributes1 = attributes1;
	}

	public String getEntityName2() {
		return entityName2;
	}

	public void setEntityName2(String entityName2) {
		this.entityName2 = entityName2;
	}

	public List<String> getAttributes2() {
		return attributes2;
	}

	public void setAttributes2(List<String> attributes2) {
		this.attributes2 = attributes2;
	}

}
