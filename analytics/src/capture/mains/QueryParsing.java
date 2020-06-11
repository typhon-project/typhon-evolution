package capture.mains;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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
import query.Insert;
import query.Join;
import query.Query;
import typhonml.Attribute;
import typhonml.Entity;
import typhonml.EntityAttributeKind;
import typhonml.Relation;

public class QueryParsing {
	private static Evaluator evaluator = null;
	private static IValueFactory vf = null;

	static {
		PropertyConfigurator.configure(
				System.getProperty("user.dir") + File.separator + "resources" + File.separator + "log4j.properties");

	}
	
	public static Logger logger = Logger.getLogger(QueryParsing.class);

	public static boolean init() {
		try {
			ISourceLocation root = URIUtil.createFileLocation(
					QueryParsing.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			PathConfig pcfg = PathConfig.fromSourceProjectRascalManifest(root);
			
			GlobalEnvironment heap = new GlobalEnvironment();
//			evaluator = new Evaluator(ValueFactoryFactory.getValueFactory(), new PrintWriter(System.err, true),
//					new PrintWriter(System.out, false), new ModuleEnvironment("$typhonql$", heap), heap);
			
			evaluator = new Evaluator(ValueFactoryFactory.getValueFactory(), null, new PrintStream(System.err, true), new PrintStream(System.out, true), new ModuleEnvironment("$typhonql$", heap), heap);
			
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
			e.printStackTrace();
			logger.error("Impossible to initialize the QueryParsing plugin\nCause:\n" + e.getCause());
			return false;
		}
	}

	public static void main(String[] args) throws URISyntaxException, IOException {
		logger.info("Query parsing");
		if (!init()) {
			logger.error("Query Parsing plugin exit");
			return;
		}
		System.out.println("WB Init...");
		TyphonModel.initWebService(ConsumePostEvents.WEBSERVICE_URL, ConsumePostEvents.WEBSERVICE_USERNAME, ConsumePostEvents.WEBSERVICE_PASSWORD);
		System.out.println("Initialized!");
		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.println("Enter your query:");
			String query = in.nextLine();

			try {
				eval(query, TyphonModel.getCurrentModel());
			} catch (Exception | Error e) {
				logger.error("Problem when analyzing: " + query);
				e.printStackTrace();
			}
		}

	}

	public static void analyzeJoin(Join join, TyphonModel model) {
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
				EntityAttributeKind attribute = model.getAttributeFromNameInEntity(attrRel, entity);
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

	public static void analyzeAtttributeSelector(AttributeSelector sel, TyphonModel model) {
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

	public static Query deserializeQuery(String s) throws IOException, ClassNotFoundException {
		try {
			byte[] data = Base64.getDecoder().decode(s);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			return (Query) o;
		} catch (Exception | Error e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String serializeQuery(Query q) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(q);
			oos.close();
			return Base64.getEncoder().encodeToString(baos.toByteArray());
		} catch (Exception | Error e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Query eval(String query, TyphonModel m) {
		logger.debug("Eval:" + query);
		IValue v = evaluator.getEvaluator().call("parseQuery", vf.string(query));
		logger.debug("Eval done");
//		Query q = parseResult(v, m);
		Query q2 = new Query();
		parseResult(v, q2);
		String serializedQuery = serializeQuery(q2);
		q2.setSerializedQuery(serializedQuery);
		q2.setModel(m);

		logger.info("Query analyzed: " + query);
		q2.print();
		return q2;
	}

	private static String toString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	private static Query parseResult(IValue v, Query res) {
		Set<String> allEntities = new HashSet<String>();
		res.setAllEntities(allEntities);

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
		allEntities.addAll(mainEntities);

		Iterator<IValue> joinIterator = ((IList) queryData.get(1)).iterator();
		while (joinIterator.hasNext()) {
			ITuple join = (ITuple) joinIterator.next();
			String entityName1 = ((IString) join.get(0)).getValue();
			List<String> attrs1 = getList(join.get(1));
			String entityName2 = ((IString) join.get(2)).getValue();
			List<String> attrs2 = getList(join.get(3));
			Join j = new Join(entityName1, attrs1, entityName2, attrs2);
			res.addJoin(j);

			if (entityName1 != null)
				allEntities.add(entityName1);
			if (entityName2 != null)
				allEntities.add(entityName2);
		}

		Iterator<IValue> attrSelectorIterator = ((IList) queryData.get(2)).iterator();
		while (attrSelectorIterator.hasNext()) {
			ITuple comp = (ITuple) attrSelectorIterator.next();
			String entityName = ((IString) comp.get(0)).getValue();
			List<String> attrs = getList(comp.get(1));
			AttributeSelector ac = new AttributeSelector(entityName, attrs);
			res.addAttributeSelector(ac);
			if (entityName != null)
				allEntities.add(entityName);
		}

		IList insertList = ((IList) queryData.get(3));
		res.setInserts(getInserts(insertList));

		for (Insert i : res.getInserts())
			if (i.getEntityName() != null)
				allEntities.add(i.getEntityName());

		return res;
	}

	private static Query parseResult(IValue v, TyphonModel m) {

		Query res = new Query();
		res.setModel(m);
		parseResult(v, res);

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
