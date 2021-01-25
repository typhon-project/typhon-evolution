package recommendations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.Attribute;

import capture.mains.AttributeSelector;
import capture.mains.QueryParsing;
import model.TyphonModel;
import query.Join;
import query.Query;
import query.WellFormedJoin;
import typhonml.Cardinality;
import typhonml.CustomDataType;
import typhonml.DataType;
import typhonml.Database;
import typhonml.DocumentDB;
import typhonml.Entity;
import typhonml.EntityAttributeKind;
import typhonml.IndexSpec;
import typhonml.NamedElement;
import typhonml.Relation;
import typhonml.RelationalDB;
import typhonml.Table;
import typhonml.impl.AttributeImpl;
import typhonml.impl.ModelImpl;

public class RecommendationMgr {
	
	public static void main(String[] args) {
		String query = "from AppData ad, ProcessedWarnings pw, MeteologixWarnings mw select mw.warningType, mw.severity, pw.WEATHER_EVENT_INTENSITY, pw.WEATHER_EVENT, ad.vehicle_position where pw.DATE < \"?\" && pw.DATE > \"?\" && ad.timeStamp == \"?\" && mw.time_start < \"?\" && mw.time_end > \"?\" && distance (ad.vehicle_position, pw.LOCATION) < \"?\" && ad.vehicle_position & mw.area";
		TyphonModel.initWebService("http://168.119.234.158:8080", "admin", "admin1@");
		TyphonModel m = TyphonModel.getCurrentModel();
		QueryParsing.init();
		Query q = QueryParsing.eval(query, m);
		List<Recommendation> list = getRecommendations(q);
		XorRecommendation r = (XorRecommendation) list.get(list.size() - 1);
		System.out.println(r.getEvolutionOperator());
	}

	public static List<Recommendation> getRecommendations(Query query) {
		if (query != null) {

			switch (query.getQueryType()) {
			case "SELECT":
				return getSelectRecommendations(query);
			case "UPDATE":
				return getUpdateRecommendations(query);
			case "DELETE":
				return getDeleteRecommendations(query);
			case "INSERT":
				return getInsertRecommendations(query);
			}
		}

		return new ArrayList<Recommendation>();
	}

	private static List<Recommendation> getInsertRecommendations(Query query) {
		return new ArrayList<Recommendation>();
	}

	private static List<Recommendation> getAttributeSelectorsRecommendations(Query query) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		for (AttributeSelector sel : query.getAllAttributeSelectors()) {
			res.addAll(getAttributeSelectorRecommendation(query, sel));

		}
		return res;
	}

	private static List<Recommendation> getAttributeSelectorRecommendation(Query query, AttributeSelector sel) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		String entityName = sel.getEntityName();
		if (sel.getAttributes().size() == 1) {
			String attr = sel.getAttributes().get(0);
			if (!hasIndex(query, entityName, attr)) {
				Entity ent = query.getModel().getEntityTypeFromName(entityName);
				if (ent != null) {
					Database db = query.getModel().getPhysicalDatabase(ent);
					NamedElement el = query.getModel().getPhysicalEntity(ent);
					if (db != null && el != null && !attr.equals("@id"))
						res.add(new IndexRecommendation(db.getName(), el.getName(), entityName, attr));
				}
			}
		}

		return res;
	}

	private static boolean hasIndex(Query query, String entityName, String attr) {
		// TODO

		for (Database d : query.getModel().getModel().getDatabases()) {
			if (d instanceof RelationalDB) {
				RelationalDB db = (RelationalDB) d;
				for (Table table : db.getTables()) {
					Entity entity = table.getEntity();
					if (entity.getName().equals(entityName)) {
						IndexSpec index = table.getIndexSpec();
						if (index != null) {
							for (typhonml.Attribute a : index.getAttributes())
								if (a.getName().equals(attr))
									return true;
						}
					}

				}
			}

			// TODO
			if (d instanceof DocumentDB) {
				DocumentDB db = (DocumentDB) d;
				for (typhonml.Collection collection : db.getCollections()) {
					Entity entity = collection.getEntity();
					if (entity.getName().equals(entityName)) {
						// TODO cannot access indexes!
					}
				}
			}
		}

		return false;
	}

	private static List<Recommendation> getDeleteRecommendations(Query query) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		res.addAll(getAttributeSelectorsRecommendations(query));
		res.addAll(getJoinsRecommendations(query.getModel(), WellFormedJoin.extractWellFormedJoins(query)));
		return res;
	}

	private static List<Recommendation> getUpdateRecommendations(Query query) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		res.addAll(getAttributeSelectorsRecommendations(query));
		res.addAll(getJoinsRecommendations(query.getModel(), WellFormedJoin.extractWellFormedJoins(query)));
		return res;
	}

	private static List<Recommendation> getSelectRecommendations(Query query) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		res.addAll(getAttributeSelectorsRecommendations(query));
		List<WellFormedJoin> wellFormedJoins = WellFormedJoin.extractWellFormedJoins(query);
		res.addAll(getJoinsRecommendations(query.getModel(), wellFormedJoins));
		if ((wellFormedJoins == null || wellFormedJoins.size() == 0) && query.getMainEntities().size() > 1) {
			// join between several entities which are performed without specifying an ML
			// rel
			// ex: from User u, Order o select u,o
			// ex: from User u, Product p select u, o where u.date < p.date

			// the possible recommendation will be to migrate all the concerned entities to
			// the same backend.
			res.addAll(getMisFormedJoinRecommendation(query.getModel(), query.getMainEntities()));
		}
		return res;
	}

	private static List<Recommendation> getMisFormedJoinRecommendation(TyphonModel model, List<String> mainEntities) {
		List<Recommendation> res = new ArrayList<Recommendation>();

		List<Entity> entities = new ArrayList<Entity>();
		List<Database> databases = new ArrayList<Database>();

		List<Database> uniqueDatabasesList = new ArrayList<Database>();
		for (String entityName : mainEntities) {
			Entity ent = model.getEntityTypeFromName(entityName);
			entities.add(ent);
			Database db = model.getPhysicalDatabase(ent);
			if (db != null) {
				boolean present = false;
				for (Database d : uniqueDatabasesList)
					if (d == db) {
						present = true;
						break;
					}
				if (!present)
					uniqueDatabasesList.add(db);
			}
			databases.add(db);
		}

		if (uniqueDatabasesList.size() > 1) { // more than one db
			XorRecommendation xor = new XorRecommendation();
			for (Database dbToMigrate : uniqueDatabasesList) {
				// one migrate all the entities to dbToMigrate (AndRecommendation)
				AndRecommendation rec = new AndRecommendation();
				for (int i = 0; i < entities.size(); i++) {
					Entity ent = entities.get(i);
					Database database = databases.get(i);
					if (dbToMigrate != database) {
						// we must migrate ent
						MigrateEntityRecommendation migrate = new MigrateEntityRecommendation(ent, dbToMigrate);
						rec.getRecommendations().add(migrate);
					}
				}

				if (rec.getRecommendations().size() > 1)
					xor.getRecommendations().add(rec);
				if (rec.getRecommendations().size() == 1)
					xor.getRecommendations().add(rec.getRecommendations().get(0));

			}

			if (xor.getRecommendations().size() == 1)
				res.add(xor.getRecommendations().get(0));
			if (xor.getRecommendations().size() > 1)
				res.add(xor);

		}

		return res;
	}

	private static List<Recommendation> getJoinsRecommendations(TyphonModel m,
			List<WellFormedJoin> extractWellFormedJoins) {
		List<Recommendation> res = new ArrayList<Recommendation>();

		for (WellFormedJoin j : extractWellFormedJoins) {
			res.addAll(getJoinRecommendations(m, j));
		}

		return res;
	}

	private static List<Recommendation> getJoinRecommendations(TyphonModel m, WellFormedJoin j) {
		List<Recommendation> res = new ArrayList<Recommendation>();

		Entity ent1 = j.getEntity1();
		NamedElement el1 = m.getPhysicalEntity(ent1);

		int maxCard1 = j.getAttributeRel().getCardinality().getValue();
		Relation opposite = m.getOpposite(j.getAttributeRel());

		Entity ent2 = j.getEntity2();
		NamedElement el2 = m.getPhysicalEntity(ent2);
		int maxCard2 = opposite == null ? 2 : opposite.getCardinality().getValue();

		res.addAll(getJoinRecommendation(m, ent1, el1, maxCard1, j.getAttributeRel(), opposite, ent2, el2, maxCard2));

		return res;
	}

	private static List<Recommendation> getJoinRecommendation(TyphonModel model, Entity ent1, NamedElement el1,
			int maxCard1, Relation attributeRel, Relation opposite, Entity ent2, NamedElement el2, int maxCard2) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		if (el1 == null || el2 == null)
			return res;

		if (el1 instanceof Table && el2 instanceof Table) {
			Database db1 = model.getPhysicalDatabase(ent1);
			Database db2 = model.getPhysicalDatabase(ent2);

			if (db1 == db2)
				res.addAll(R_R(model, ent1, maxCard1, attributeRel, opposite, ent2, maxCard2));
			else {
				// tables stored in different databases
				res.addAll(R_R$(model, ent1, maxCard1, attributeRel, opposite, ent2, maxCard2));
			}
		}

		if (el1 instanceof Table && el2 instanceof typhonml.Collection)
			res.addAll(R_D(model, ent1, maxCard1, attributeRel, opposite, ent2, maxCard2));

		if (el1 instanceof typhonml.Collection && el2 instanceof Table)
			res.addAll(D_R(model, ent1, maxCard1, attributeRel, opposite, ent2, maxCard2));

		if (el1 instanceof typhonml.Collection && el2 instanceof typhonml.Collection) {
			typhonml.Collection coll1 = (typhonml.Collection) el1;
			typhonml.Collection coll2 = (typhonml.Collection) el2;
			Database db1 = model.getPhysicalDatabase(coll1.getEntity());
			Database db2 = model.getPhysicalDatabase(coll2.getEntity());

			if (db1 == db2)
				res.addAll(D_D(model, ent1, maxCard1, attributeRel, opposite, ent2, maxCard2));
			else {
				// collections stored in different databases
				res.addAll(D_D$(model, ent1, maxCard1, attributeRel, opposite, ent2, maxCard2));
			}
		}

		return res;
	}

	private static List<Recommendation> D_D(TyphonModel model, Entity ent1, int maxCard1, Relation rel, Relation rel2,
			Entity ent2, int maxCard2) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		if (maxCard1 == 1 && maxCard2 == 2) {
			Recommendation r = D1_ND(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 1 && maxCard2 == 1) {
			Recommendation r = D1_1D(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 1) {
			Recommendation r = DN_1D(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 2) {
			Recommendation r = DN_ND(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		return res;
	}

	private static List<Recommendation> D_D$(TyphonModel model, Entity ent1, int maxCard1, Relation rel, Relation rel2,
			Entity ent2, int maxCard2) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		if (maxCard1 == 1 && maxCard2 == 2) {
			Recommendation r = D1_ND$(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 1 && maxCard2 == 1) {
			Recommendation r = D1_1D$(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 1) {
			Recommendation r = DN_1D$(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 2) {
			Recommendation r = DN_ND$(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		return res;
	}

	private static Recommendation DN_ND(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 1 recommendation: migrating both entities into relational database(s)

		List<Recommendation> list = new ArrayList<Recommendation>();
		for (Database db : model.getModel().getDatabases())
			if (db instanceof RelationalDB) {
				RelationalDB rdb = (RelationalDB) db;
				MigrateEntityRecommendation r21 = new MigrateEntityRecommendation(ent1, rdb);
				MigrateEntityRecommendation r22 = new MigrateEntityRecommendation(ent2, rdb);
				AndRecommendation ar = new AndRecommendation(r21, r22);
				list.add(ar);
			}

		Recommendation r2 = null;
		if (list.size() == 1) {
			r2 = list.get(0);
		}

		if (list.size() > 1) {
			r2 = new XorRecommendation(list.toArray(new Recommendation[list.size()]));
		}

		return r2;
	}

	private static Recommendation DN_ND$(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 2 xor recommendations:
		// 1) migrating one entity to the database of the other one
		// 2) migrating both entities into relational database(s)

		// 1)
		Database db1 = model.getPhysicalDatabase(ent1);
		Database db2 = model.getPhysicalDatabase(ent2);
		MigrateEntityRecommendation r11 = new MigrateEntityRecommendation(ent1, db2);
		MigrateEntityRecommendation r12 = new MigrateEntityRecommendation(ent2, db1);
		XorRecommendation r1 = new XorRecommendation(r11, r12);

		// 2)
		List<Recommendation> list = new ArrayList<Recommendation>();
		for (Database db : model.getModel().getDatabases())
			if (db instanceof RelationalDB) {
				RelationalDB rdb = (RelationalDB) db;
				MigrateEntityRecommendation r21 = new MigrateEntityRecommendation(ent1, rdb);
				MigrateEntityRecommendation r22 = new MigrateEntityRecommendation(ent2, rdb);
				AndRecommendation ar = new AndRecommendation(r21, r22);
				list.add(ar);
			}

		Recommendation r2 = null;
		if (list.size() == 1) {
			r2 = list.get(0);
		}

		if (list.size() > 1) {
			r2 = new XorRecommendation(list.toArray(new Recommendation[list.size()]));
		}

		return new XorRecommendation(r1, r2);
	}

	private static Recommendation DN_1D(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 2 XOR recommendations
		// 1) merging ent2 into ent1
		// 2) migrating both entities into relational database(s) => faster SQL joins

		// 1)
		MergeEntitiesRecommendation r1 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent2, rel2, rel) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r1 = new MergeEntitiesRecommendation(ent1, ent2, rel);
		}

		List<Recommendation> list = new ArrayList<Recommendation>();
		for (Database db : model.getModel().getDatabases())
			if (db instanceof RelationalDB) {
				RelationalDB rdb = (RelationalDB) db;
				MigrateEntityRecommendation r21 = new MigrateEntityRecommendation(ent1, rdb);
				MigrateEntityRecommendation r22 = new MigrateEntityRecommendation(ent2, rdb);
				AndRecommendation ar = new AndRecommendation(r21, r22);
				list.add(ar);
			}

		Recommendation r2 = null;
		if (list.size() == 1) {
			r2 = list.get(0);
		}

		if (list.size() > 1) {
			r2 = new XorRecommendation(list.toArray(new Recommendation[list.size()]));
		}

		if (r1 != null && r2 == null)
			return r1;
		if (r1 == null && r2 != null)
			return r2;

		if (r1 != null && r2 != null)
			return new XorRecommendation(r1, r2);

		return null;
	}

	private static Recommendation DN_1D$(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 3 XOR recommendations
		// 1) merging ent2 into ent1
		// 2) migrating one entity to the database of the other one
		// 3) migrating both entities into relational database(s) => faster SQL joins

		// 1)
		MergeEntitiesRecommendation r1 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent2, rel2, rel) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r1 = new MergeEntitiesRecommendation(ent1, ent2, rel);
		}

		// 2)
		Database db1 = model.getPhysicalDatabase(ent1);
		Database db2 = model.getPhysicalDatabase(ent2);
		MigrateEntityRecommendation r21 = new MigrateEntityRecommendation(ent1, db2);
		MigrateEntityRecommendation r22 = new MigrateEntityRecommendation(ent2, db1);
		XorRecommendation r2 = new XorRecommendation(r21, r22);

		// 3)
		List<Recommendation> list = new ArrayList<Recommendation>();
		for (Database db : model.getModel().getDatabases())
			if (db instanceof RelationalDB) {
				RelationalDB rdb = (RelationalDB) db;
				MigrateEntityRecommendation r31 = new MigrateEntityRecommendation(ent1, rdb);
				MigrateEntityRecommendation r32 = new MigrateEntityRecommendation(ent2, rdb);
				AndRecommendation ar = new AndRecommendation(r31, r32);
				list.add(ar);
			}

		Recommendation r3 = null;
		if (list.size() == 1) {
			r3 = list.get(0);
		}

		if (list.size() > 1) {
			r3 = new XorRecommendation(list.toArray(new Recommendation[list.size()]));
		}

		if (r1 != null && r3 == null)
			return new XorRecommendation(r1, r2);
		if (r1 == null && r3 != null)
			return new XorRecommendation(r2, r3);

		if (r1 != null && r3 != null)
			return new XorRecommendation(r1, r2, r3);

		return r2;
	}

	private static Recommendation D1_1D(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 2 XOR recommendations:
		// 1) merge e1 into e2
		// 2) merge e2 into e1

		MergeEntitiesRecommendation r1 = null;
		MergeEntitiesRecommendation r2 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent1, rel, rel2) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r1 = new MergeEntitiesRecommendation(ent2, ent1, rel);
		}

		if (ent1 != ent2 && !model.hasOtherRelations(model, ent2, rel2, rel) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r2 = new MergeEntitiesRecommendation(ent1, ent2, rel);
		}

		if (r1 != null && r2 != null)
			return new XorRecommendation(r1, r2);

		if (r1 != null)
			return r1;
		if (r2 != null)
			return r2;

		return null;
	}

	private static Recommendation D1_1D$(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 3 XOR recommendations:
		// 1) merge e1 into e2
		// 2) merge e2 into e1
		// 3) migrating one entity to the database of the other one

		MergeEntitiesRecommendation r1 = null;
		MergeEntitiesRecommendation r2 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent1, rel, rel2) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r1 = new MergeEntitiesRecommendation(ent2, ent1, rel);
		}

		if (ent1 != ent2 && !model.hasOtherRelations(model, ent2, rel2, rel) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r2 = new MergeEntitiesRecommendation(ent1, ent2, rel);
		}

		Database db1 = model.getPhysicalDatabase(ent1);
		Database db2 = model.getPhysicalDatabase(ent2);
		MigrateEntityRecommendation r31 = new MigrateEntityRecommendation(ent1, db2);
		MigrateEntityRecommendation r32 = new MigrateEntityRecommendation(ent2, db1);
		XorRecommendation r3 = new XorRecommendation(r31, r32);

		if (r1 != null && r2 != null)
			return new XorRecommendation(r1, r2, r3);

		if (r1 != null)
			return new XorRecommendation(r1, r3);
		if (r2 != null)
			return new XorRecommendation(r2, r3);

		return r3;
	}

	private static Recommendation D1_ND(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 2 XOR recommendations
		// 1) merging ent1 into ent2
		// 2) migrating both entities into relational database(s) => faster SQL joins

		// 1)
		MergeEntitiesRecommendation r1 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent1, rel, rel2) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r1 = new MergeEntitiesRecommendation(ent2, ent1, rel);
		}

		List<Recommendation> list = new ArrayList<Recommendation>();
		for (Database db : model.getModel().getDatabases())
			if (db instanceof RelationalDB) {
				RelationalDB rdb = (RelationalDB) db;
				MigrateEntityRecommendation r21 = new MigrateEntityRecommendation(ent1, rdb);
				MigrateEntityRecommendation r22 = new MigrateEntityRecommendation(ent2, rdb);
				AndRecommendation ar = new AndRecommendation(r21, r22);
				list.add(ar);
			}

		Recommendation r2 = null;
		if (list.size() == 1) {
			r2 = list.get(0);
		}

		if (list.size() > 1) {
			r2 = new XorRecommendation(list.toArray(new Recommendation[list.size()]));
		}

		if (r1 != null && r2 == null)
			return r1;
		if (r1 == null && r2 != null)
			return r2;

		if (r1 != null && r2 != null)
			return new XorRecommendation(r1, r2);

		return null;

	}

	private static Recommendation D1_ND$(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 3 XOR recommendations
		// 1) merging ent1 into ent2
		// 2) migrating one entity to the document database of the other one
		// 3) migrating both entities into relational database(s) => faster SQL joins

		// 1)
		MergeEntitiesRecommendation r1 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent1, rel, rel2) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r1 = new MergeEntitiesRecommendation(ent2, ent1, rel);
		}

		// 2)
		Recommendation r2;
		Database db1 = model.getPhysicalDatabase(ent1);
		Database db2 = model.getPhysicalDatabase(ent2);
		MigrateEntityRecommendation mer1 = new MigrateEntityRecommendation(ent1, db2);
		MigrateEntityRecommendation mer2 = new MigrateEntityRecommendation(ent2, db1);
		r2 = new XorRecommendation(mer1, mer2);

		// 3)
		List<Recommendation> list = new ArrayList<Recommendation>();
		for (Database db : model.getModel().getDatabases())
			if (db instanceof RelationalDB) {
				RelationalDB rdb = (RelationalDB) db;
				MigrateEntityRecommendation r21 = new MigrateEntityRecommendation(ent1, rdb);
				MigrateEntityRecommendation r22 = new MigrateEntityRecommendation(ent2, rdb);
				AndRecommendation ar = new AndRecommendation(r21, r22);
				list.add(ar);
			}

		Recommendation r3 = null;
		if (list.size() == 1) {
			r3 = list.get(0);
		}

		if (list.size() > 1) {
			r3 = new XorRecommendation(list.toArray(new Recommendation[list.size()]));
		}

		if (r1 == null && r3 == null)
			return r2;

		if (r1 != null && r3 == null)
			return new XorRecommendation(r1, r2);

		if (r1 == null && r3 != null)
			return new XorRecommendation(r2, r3);

		return new XorRecommendation(r1, r2, r3);

	}

	private static List<Recommendation> D_R(TyphonModel model, Entity ent1, int maxCard1, Relation rel, Relation rel2,
			Entity ent2, int maxCard2) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		if (maxCard1 == 1 && maxCard2 == 2) {
			Recommendation r = D1_NR(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 1 && maxCard2 == 1) {
			Recommendation r = D1_1R(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 1) {
			Recommendation r = DN_1R(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 2) {
			Recommendation r = DN_NR(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		return res;
	}

	private static Recommendation DN_NR(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 2 XOR recommendations:
		// 1) migrating ent1 into relational database
		// 2) migrating ent2 into document db
		Database db = model.getPhysicalDatabase(ent1);
		Database db2 = model.getPhysicalDatabase(ent2);

		// 1)
		MigrateEntityRecommendation r1 = new MigrateEntityRecommendation(ent1, db2);

		// 2)
		MigrateEntityRecommendation r2 = new MigrateEntityRecommendation(ent2, db);

		return new XorRecommendation(r1, r2);
	}

	private static Recommendation DN_1R(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 2 XOR recommendations:
		// 1) migrating ent1 in relational database => faster SQL join
		// 2) merging ent2 into ent1

		Database db = model.getPhysicalDatabase(ent2);

		// 1)
		MigrateEntityRecommendation r1 = new MigrateEntityRecommendation(ent1, db);

		// 2)
		MergeEntitiesRecommendation r2 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent2, rel2, rel) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r2 = new MergeEntitiesRecommendation(ent1, ent2, rel);
		}

		if (r2 == null)
			return r1;

		return new XorRecommendation(r1, r2);
	}

	private static Recommendation D1_1R(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 3 XOR recommendation
		// 1) migrating ent1 in relational database => faster SQL join
		// 2) merging ent1 into ent2
		// 3) merging ent2 into ent1

		Database db = model.getPhysicalDatabase(ent2);

		// 1)
		MigrateEntityRecommendation r1 = new MigrateEntityRecommendation(ent1, db);

		// 2)
		MergeEntitiesRecommendation r2 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent1, rel, rel2) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r2 = new MergeEntitiesRecommendation(ent2, ent1, rel);
		}

		// 3)
		MergeEntitiesRecommendation r3 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent2, rel2, rel) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r3 = new MergeEntitiesRecommendation(ent1, ent2, rel);
		}

		if (r2 == null && r3 == null)
			return r1;

		if (r2 != null && r3 == null)
			return new XorRecommendation(r1, r2);

		if (r2 == null && r3 != null)
			return new XorRecommendation(r1, r3);

		return new XorRecommendation(r1, r2, r3);
	}

	private static Recommendation D1_NR(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 3 XOR recommendations:
		// 1) migrating ent1 in relational database => faster SQL join
		// 2) migrating ent2 in document db => faster join
		// 3) migrating ent2 in document db + merging ent1 into ent2
		Database db = model.getPhysicalDatabase(ent1);
		Database db2 = model.getPhysicalDatabase(ent2);

		// 1)
		MigrateEntityRecommendation r1 = new MigrateEntityRecommendation(ent1, db2);

		// 2)
		MigrateEntityRecommendation r2 = new MigrateEntityRecommendation(ent2, db);

		// 3)

		AndRecommendation r3 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent1, rel, rel2) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			MigrateEntityRecommendation r31 = new MigrateEntityRecommendation(ent2, db);
			MergeEntitiesRecommendation r32 = new MergeEntitiesRecommendation(ent2, ent1, rel);
			r3 = new AndRecommendation(r31, r32);
		}

		if (r3 == null)
			return new XorRecommendation(r1, r2);

		return new XorRecommendation(r1, r2, r3);
	}

	private static List<Recommendation> R_D(TyphonModel model, Entity ent1, int maxCard1, Relation rel, Relation rel2,
			Entity ent2, int maxCard2) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		if (maxCard1 == 1 && maxCard2 == 2) {
			Recommendation r = R1_ND(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 1 && maxCard2 == 1) {
			Recommendation r = R1_1D(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 1) {
			Recommendation r = RN_1D(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 2) {
			Recommendation r = RN_ND(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		return res;
	}

	private static Recommendation RN_ND(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 2 XOR recommendations:
		// 1) migrating ent2 into relational database
		// 2) migraitng ent1 into document db
		Database db = model.getPhysicalDatabase(ent1);
		Database db2 = model.getPhysicalDatabase(ent2);

		// 1)
		MigrateEntityRecommendation r1 = new MigrateEntityRecommendation(ent2, db);

		// 2)
		MigrateEntityRecommendation r2 = new MigrateEntityRecommendation(ent1, db2);

		return new XorRecommendation(r1, r2);

	}

	private static Recommendation R1_1D(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 3 XOR recommendation
		// 1) migrating ent2 in relational database => faster SQL join
		// 2) merging ent1 into ent2
		// 3) merging ent2 into ent1

		Database db = model.getPhysicalDatabase(ent1);

		// 1)
		MigrateEntityRecommendation r1 = new MigrateEntityRecommendation(ent2, db);

		// 2)
		MergeEntitiesRecommendation r2 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent1, rel, rel2) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r2 = new MergeEntitiesRecommendation(ent2, ent1, rel);
		}

		// 3)
		MergeEntitiesRecommendation r3 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent2, rel2, rel) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r3 = new MergeEntitiesRecommendation(ent1, ent2, rel);
		}

		if (r2 == null && r3 == null)
			return r1;

		if (r2 != null && r3 == null)
			return new XorRecommendation(r1, r2);

		if (r2 == null && r3 != null)
			return new XorRecommendation(r1, r3);

		return new XorRecommendation(r1, r2, r3);

	}

	private static Recommendation RN_1D(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 3 XOR recommendations:
		// 1) migrating ent2 in relational database => faster SQL join
		// 2) migrating ent1 in document db => faster join
		// 3) migrating ent1 in document db + merging ent2 into ent1

		Database db = model.getPhysicalDatabase(ent1);
		Database db2 = model.getPhysicalDatabase(ent2);

		// 1)
		MigrateEntityRecommendation r1 = new MigrateEntityRecommendation(ent2, db);

		// 2)
		MigrateEntityRecommendation r2 = new MigrateEntityRecommendation(ent1, db2);

		// 3)

		AndRecommendation r3 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent2, rel2, rel) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			MigrateEntityRecommendation r31 = new MigrateEntityRecommendation(ent1, db2);
			MergeEntitiesRecommendation r32 = new MergeEntitiesRecommendation(ent1, ent2, rel);
			r3 = new AndRecommendation(r31, r32);
		}

		if (r3 == null)
			return new XorRecommendation(r1, r2);

		return new XorRecommendation(r1, r2, r3);

	}

	private static Recommendation R1_ND(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// 2 XOR recommendations:
		// 1) migrating ent2 in relational database => faster SQL join
		// 2) merging ent1 into ent2

		Database db = model.getPhysicalDatabase(ent1);

		// 1)
		MigrateEntityRecommendation r1 = new MigrateEntityRecommendation(ent2, db);

		// 2)
		MergeEntitiesRecommendation r2 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent1, rel, rel2) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r2 = new MergeEntitiesRecommendation(ent2, ent1, rel);
		}

		if (r2 == null)
			return r1;

		return new XorRecommendation(r1, r2);

	}

	private static List<Recommendation> R_R(TyphonModel model, Entity ent1, int maxCard1, Relation rel, Relation rel2,
			Entity ent2, int maxCard2) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		if (maxCard1 == 1 && maxCard2 == 2) {
			Recommendation r = R1_NR(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 1 && maxCard2 == 1) {
			Recommendation r = R1_1R(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 1) {
			Recommendation r = RN_1R(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 2) {
			Recommendation r = RN_NR(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		return res;

	}

	private static List<Recommendation> R_R$(TyphonModel model, Entity ent1, int maxCard1, Relation rel, Relation rel2,
			Entity ent2, int maxCard2) {
		List<Recommendation> res = new ArrayList<Recommendation>();
		if (maxCard1 == 1 && maxCard2 == 2) {
			Recommendation r = R1_NR$(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 1 && maxCard2 == 1) {
			Recommendation r = R1_1R$(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 1) {
			Recommendation r = RN_1R$(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		if (maxCard1 == 2 && maxCard2 == 2) {
			Recommendation r = RN_NR$(model, ent1, rel, rel2, ent2);
			if (r != null)
				res.add(r);
		}

		return res;

	}

	private static Recommendation RN_NR(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// no recommendations
		return null;
	}

	private static Recommendation RN_NR$(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// XOR condition: migrating one entity to the database of the other one
		Database db1 = model.getPhysicalDatabase(ent1);
		Database db2 = model.getPhysicalDatabase(ent2);

		MigrateEntityRecommendation r1 = new MigrateEntityRecommendation(ent1, db2);
		MigrateEntityRecommendation r2 = new MigrateEntityRecommendation(ent2, db1);

		return new XorRecommendation(r1, r2);
	}

	private static Recommendation RN_1R(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// no recommendations
		return null;
	}

	private static Recommendation RN_1R$(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// XOR condition: migrating one entity to the database of the other one
		Database db1 = model.getPhysicalDatabase(ent1);
		Database db2 = model.getPhysicalDatabase(ent2);

		MigrateEntityRecommendation r1 = new MigrateEntityRecommendation(ent1, db2);
		MigrateEntityRecommendation r2 = new MigrateEntityRecommendation(ent2, db1);

		return new XorRecommendation(r1, r2);
	}

	/**
	 * 
	 * @param model
	 * @param ent1
	 * @param rel   rel != null
	 * @param rel2  rel2 can be equal to null
	 * @param ent2
	 * @return
	 */
	private static Recommendation R1_1R(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		MergeEntitiesRecommendation r1 = null;
		MergeEntitiesRecommendation r2 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent1, rel, rel2) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r1 = new MergeEntitiesRecommendation(ent2, ent1, rel);
		}

		if (ent1 != ent2 && !model.hasOtherRelations(model, ent2, rel2, rel) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r2 = new MergeEntitiesRecommendation(ent1, ent2, rel);
		}

		if (r1 != null && r2 != null)
			return new XorRecommendation(r1, r2);

		if (r1 != null)
			return r1;
		if (r2 != null)
			return r2;

		return null;
	}

	private static Recommendation R1_1R$(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		MergeEntitiesRecommendation r1 = null;
		MergeEntitiesRecommendation r2 = null;
		if (ent1 != ent2 && !model.hasOtherRelations(model, ent1, rel, rel2) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r1 = new MergeEntitiesRecommendation(ent2, ent1, rel);
		}

		if (ent1 != ent2 && !model.hasOtherRelations(model, ent2, rel2, rel) && !model.isContainmentRelation(rel)
				&& !model.isContainmentRelation(rel2)) {
			r2 = new MergeEntitiesRecommendation(ent1, ent2, rel);
		}

		if (r1 != null && r2 != null)
			return new XorRecommendation(r1, r2);

		if (r1 != null)
			return r1;
		if (r2 != null)
			return r2;

		return null;
	}

	private static Recommendation R1_NR(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// no recommendations
		return null;
	}

	private static Recommendation R1_NR$(TyphonModel model, Entity ent1, Relation rel, Relation rel2, Entity ent2) {
		// one XOR recommendation: migrating one to the database of the other one

		Database db1 = model.getPhysicalDatabase(ent1);
		Database db2 = model.getPhysicalDatabase(ent2);

		MigrateEntityRecommendation r1 = new MigrateEntityRecommendation(ent1, db2);
		MigrateEntityRecommendation r2 = new MigrateEntityRecommendation(ent2, db1);

		return new XorRecommendation(r1, r2);
	}

}
