
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
import nl.cwi.swat.typhonql.workingset.Entity;
import nl.cwi.swat.typhonql.workingset.WorkingSet;

public class QueryParsing {
	private static Evaluator evaluator = null;
	private static IValueFactory vf = null;

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

			System.out.println("Importing plugin ...");
			evaluator.doImport(null, "TyphonQLAnalytics");
			vf = ValueFactoryFactory.getValueFactory();
			System.out.println("Plugin imported with success");
			return true;
		} catch (Exception | Error e) {
			System.err.println("Impossible to initialize the QueryParsing plugin\nCause:\n" + e.getCause());
			return false;
		}
	}

	public static void main(String[] args) throws URISyntaxException, IOException {

		if (!init()) {
			System.err.println("Query Parsing plugin exit");
			return;
		}
		
		Scanner in = new Scanner(System.in);
		while(true) {
			 String query = in.nextLine();
			 
			 try {
				 eval(query);
			 } catch(Exception | Error e) {
				 System.err.println("Problem when analyzing: " + query);
				 e.printStackTrace();
			 }
		}

	}

	public static Query eval(String query) {
		System.out.println("Query analyzing ...");
		IValue v = evaluator.getEvaluator().call("parseQuery",
				vf.string(query));
		Query q = parseResult(v);
		System.out.println("Query analyzed");
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

		Iterator<IValue> attrComparatorIterator = ((IList) queryData.get(2)).iterator();
		while (attrComparatorIterator.hasNext()) {
			ITuple comp = (ITuple) attrComparatorIterator.next();
			String entityName = ((IString) comp.get(0)).getValue();
			List<String> attrs = getList(comp.get(1));
			AttributeComparator ac = new AttributeComparator(entityName, attrs);
			res.getAttributeComparators().add(ac);
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

	public static WorkingSet fromIValue(IValue v) {
		// map[str entity, list[Entity] entities];
		if (v instanceof IMap) {
			IMap map = (IMap) v;
			WorkingSet ws = new WorkingSet();
			Iterator<Entry<IValue, IValue>> iter = map.entryIterator();
			while (iter.hasNext()) {
				Entry<IValue, IValue> entry = iter.next();
				IString key = (IString) entry.getKey();
				IList entries = (IList) entry.getValue();
				Iterator<IValue> entryIter = entries.iterator();
				List<Entity> entities = new ArrayList<Entity>();
				while (entryIter.hasNext()) {
					IValue current = entryIter.next();
					Entity e = Entity.fromIValue(current);
					entities.add(e);
				}

				ws.put(key.getValue(), entities);
			}
			return ws;
		} else
			throw new RuntimeException("IValue does not represent a working set");

	}

}

class Query {
	private String originalQuery;
	private String normalizedQuery;
	private String displayableQuery;
	private String queryType;

	private List<String> mainEntities = new ArrayList<String>();

	private List<Join> joins = new ArrayList<Join>();
	private List<AttributeComparator> attributeComparators = new ArrayList<AttributeComparator>();
	private List<Insert> inserts = new ArrayList<Insert>();

	public void print() {
		System.out.println("Original query: " + originalQuery);
		System.out.println("Normalized query: " + normalizedQuery);
		System.out.println("Displayable query: " + displayableQuery);
		System.out.println("Query type: " + queryType);
		System.out.println("*****************************************");
		System.out.println("Main entities: " + mainEntities);
		System.out.println("*****************************************");
		if (joins.size() > 0) {
			System.out.println("Joins between entities: ");
			for (Join j : joins) {
				System.out.println("   " + j.getEntityName1() + j.getAttributes1() + " AND " + j.getEntityName2()
						+ j.getAttributes2());
			}

			System.out.println("*****************************************");
		}

		if (attributeComparators.size() > 0) {
			System.out.println("Attribute selectors:");
			for (AttributeComparator c : attributeComparators) {
				System.out.println("   " + c.getEntityName() + c.getAttributes());
			}
			System.out.println("*****************************************");
		}

		if (inserts.size() > 0) {
			System.out.println("Inserts: " + inserts);
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

	public List<AttributeComparator> getAttributeComparators() {
		return attributeComparators;
	}

	public void setAttributeComparators(List<AttributeComparator> attributeComparators) {
		this.attributeComparators = attributeComparators;
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

class AttributeComparator {
	private String entityName;
	private List<String> attributes;

	public AttributeComparator(String entityName, List<String> attributes) {
		this.entityName = entityName;
		this.attributes = attributes;
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
