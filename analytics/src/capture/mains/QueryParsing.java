package capture.mains;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.env.GlobalEnvironment;
import org.rascalmpl.interpreter.env.ModuleEnvironment;
import org.rascalmpl.interpreter.load.StandardLibraryContributor;
import org.rascalmpl.library.util.PathConfig;
import org.rascalmpl.uri.URIUtil;
import org.rascalmpl.values.ValueFactoryFactory;

import io.usethesource.vallang.IList;
import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IString;
import io.usethesource.vallang.ITuple;
import io.usethesource.vallang.IValue;
import io.usethesource.vallang.IValueFactory;
import model.TyphonModel;
import typhonml.Attribute;
import typhonml.Entity;
import typhonml.Relation;

public class QueryParsing {
	private static Evaluator evaluator = null;
	private static IValueFactory vf = null;

	static Logger logger = Logger.getLogger(QueryParsing.class);


	public static boolean init() {

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

	static void analyzeJoin(Join join, TyphonModel model) {
		Couple<List<Join>, AttributeSelector> couple1 = getImplicitJoins(join.getEntityName1(), join.getAttributes1(),
				model);
		Couple<List<Join>, AttributeSelector> couple2 = getImplicitJoins(join.getEntityName2(), join.getAttributes2(),
				model);

		if (couple1 != null) {
			List<Join> joins = couple1.getX();
			AttributeSelector as = couple1.getY();

			join.setImplicitJoins1(joins);
			join.setImplicitAttributeSelector1(as);

		}

		if (couple2 != null) {
			List<Join> joins = couple2.getX();
			AttributeSelector as = couple2.getY();

			join.setImplicitJoins2(joins);
			join.setImplicitAttributeSelector2(as);

		}

	}

	private static Couple<List<Join>, AttributeSelector> getImplicitJoins(String entityName, List<String> attrList,
			TyphonModel model) {
		Couple<List<Join>, AttributeSelector> res = null;

		AttributeSelector as = null;
		List<Join> joins = new ArrayList<Join>();
		Entity entity = model.getEntityTypeFromName(entityName);
		for (String attrRel : attrList) {
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
					logger.error("Query is obsolete: " + attrRel + " in " + entityName);
					return res;
				}
			}
		}

		if (joins.size() > 0)
			res = new Couple(joins, as);
		return res;
	}

	static void analyzeAtttributeSelector(AttributeSelector sel, TyphonModel model) {
		Couple<List<Join>, AttributeSelector> couple = getImplicitJoins(sel.getEntityName(), sel.getAttributes(),
				model);
		if (couple == null)
			return;

		List<Join> implicitJoins = couple.getX();
		AttributeSelector as = couple.getY();

		if (implicitJoins.size() > 0) {
			sel.setImplicitJoins(implicitJoins);
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
			res.addJoin(j);
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
				if (j.containsImplicitJoins1()) {
					QueryParsing.logger.debug("Implicit joins in first part: ");
					for (Join j2 : j.getImplicitJoins1())
						QueryParsing.logger.debug("   -> " + j2.getEntityName1() + j2.getAttributes1() + " AND "
								+ j2.getEntityName2() + j2.getAttributes2());

					if (j.getImplicitAttributeSelector1() != null)
						QueryParsing.logger.debug("   -> " + j.getImplicitAttributeSelector1().getEntityName()
								+ j.getImplicitAttributeSelector1().getAttributes());

				}

				if (j.containsImplicitJoins2()) {
					QueryParsing.logger.debug("Implicit joins in snd part: ");
					for (Join j2 : j.getImplicitJoins2())
						QueryParsing.logger.debug("   -> " + j2.getEntityName1() + j2.getAttributes1() + " AND "
								+ j2.getEntityName2() + j2.getAttributes2());

					if (j.getImplicitAttributeSelector2() != null)
						QueryParsing.logger.debug("   -> " + j.getImplicitAttributeSelector2().getEntityName()
								+ j.getImplicitAttributeSelector2().getAttributes());
				}
			}

			QueryParsing.logger.debug("*****************************************");
		}

		if (attributeSelectors.size() > 0) {
			QueryParsing.logger.debug("Attribute selectors:");
			for (AttributeSelector c : attributeSelectors) {
				QueryParsing.logger.debug("   " + c.getEntityName() + c.getAttributes()
						+ (c.containsImplicitJoins() ? " contains implicit joins:" : ""));
				if (c.containsImplicitJoins()) {
					for (Join j : c.getImplicitJoins())
						QueryParsing.logger.debug("   -> " + j.getEntityName1() + j.getAttributes1() + " AND "
								+ j.getEntityName2() + j.getAttributes2());
					QueryParsing.logger
							.debug("   -> " + c.getImplicitSel().getEntityName() + c.getImplicitSel().getAttributes());
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

	public void addJoin(Join join) {
		QueryParsing.analyzeJoin(join, getModel());
		joins.add(join);
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
		return implicitJoins != null && implicitJoins.size() > 0;
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

class Couple<X, Y> {
	private X x;
	private Y y;

	public Couple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public void setX(X x) {
		this.x = x;
	}

	public Y getY() {
		return y;
	}

	public void setY(Y y) {
		this.y = y;
	}
}

class Join {
	private String entityName1;
	private List<String> attributes1;
	private String entityName2;
	private List<String> attributes2;

	private List<Join> implicitJoins1;
	private AttributeSelector implicitAttributeSelector1;
	private List<Join> implicitJoins2;
	private AttributeSelector implicitAttributeSelector2;

	public boolean containsImplicitJoins1() {
		return implicitJoins1 != null && implicitJoins1.size() > 0;
	}

	public boolean containsImplicitJoins2() {
		return implicitJoins2 != null && implicitJoins2.size() > 0;
	}

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

	public List<Join> getImplicitJoins1() {
		return implicitJoins1;
	}

	public void setImplicitJoins1(List<Join> implicitJoins1) {
		this.implicitJoins1 = implicitJoins1;
	}

	public List<Join> getImplicitJoins2() {
		return implicitJoins2;
	}

	public void setImplicitJoins2(List<Join> implicitJoins2) {
		this.implicitJoins2 = implicitJoins2;
	}

	public AttributeSelector getImplicitAttributeSelector1() {
		return implicitAttributeSelector1;
	}

	public void setImplicitAttributeSelector1(AttributeSelector implicitAttributeSelector1) {
		this.implicitAttributeSelector1 = implicitAttributeSelector1;
	}

	public AttributeSelector getImplicitAttributeSelector2() {
		return implicitAttributeSelector2;
	}

	public void setImplicitAttributeSelector2(AttributeSelector implicitAttributeSelector2) {
		this.implicitAttributeSelector2 = implicitAttributeSelector2;
	}

}
