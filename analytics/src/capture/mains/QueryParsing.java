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
import typhonml.EntityAttributeKind;
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
				eval(query, TyphonModel.getCurrentModel());
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

	public static Query eval(String query, TyphonModel m) {
		IValue v = evaluator.getEvaluator().call("parseQuery", vf.string(query));
		Query q = parseResult(v, m);
		logger.info("Query analyzed: " + query);
//		q.print();
		return q;
	}

	private static Query parseResult(IValue v, TyphonModel m) {
		Query res = new Query();
		res.setModel(m);

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

