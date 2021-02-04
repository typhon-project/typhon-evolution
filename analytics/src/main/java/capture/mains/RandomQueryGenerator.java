package capture.mains;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.emf.common.util.EList;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import db.AnalyticsDB;
import model.TyphonModel;
import typhonml.Attribute;
import typhonml.DataType;
import typhonml.Entity;
import typhonml.EntityAttributeKind;
import typhonml.Relation;
import typhonml.impl.BigintTypeImpl;
import typhonml.impl.BlobTypeImpl;
import typhonml.impl.BoolTypeImpl;
import typhonml.impl.DataTypeImpl;
import typhonml.impl.DatetimeTypeImpl;
import typhonml.impl.FloatTypeImpl;
import typhonml.impl.IntTypeImpl;
import typhonml.impl.StringTypeImpl;
import typhonml.impl.TextTypeImpl;

public class RandomQueryGenerator {

	private static final int SELECT = 3;
	private static final int UPDATE = 1;
	private static final int INSERT = 2;
	private static final int DELETE = 0;

	private TyphonModel model = null;
	
	
//	public static void main(String[] args) {
//		if (!AnalyticsDB.initConnection(ConsumePostEvents.ANALYTICS_DB_IP, ConsumePostEvents.ANALYTICS_DB_PORT,
//				ConsumePostEvents.ANALYTICS_DB_USER, ConsumePostEvents.ANALYTICS_DB_PWD,
//				ConsumePostEvents.ANALYTICS_DB_NAME))
//			System.exit(1);
//		TyphonModel.initWebService(ConsumePostEvents.WEBSERVICE_URL, ConsumePostEvents.WEBSERVICE_USERNAME,
//				ConsumePostEvents.WEBSERVICE_PASSWORD);
//		RandomQueryGenerator g = new RandomQueryGenerator(TyphonModel.getCurrentModel());
//
//		for (int i = 0; i < 1000; i++) {
//			String query = g.randomQuery();
//			if (query != null)
//				System.out.println(query);
//		}
//
//	}

	public static void main2(String[] args) {
		if (!AnalyticsDB.initConnection(ConsumePostEvents.ANALYTICS_DB_IP, ConsumePostEvents.ANALYTICS_DB_PORT,
				ConsumePostEvents.ANALYTICS_DB_USER, ConsumePostEvents.ANALYTICS_DB_PWD,
				ConsumePostEvents.ANALYTICS_DB_NAME))
			System.exit(1);
		MongoDatabase db = AnalyticsDB.getDatabase();
		MongoCollection coll = db.getCollection("TyphonEntityHistory");
		FindIterable<Document> docs = coll.find();
		MongoCursor<Document> cursor = docs.iterator();
		int dataSize = 100;
		final int add = 200;
		int counter = 0;
		while (cursor.hasNext()) {
			Document doc = cursor.next();
//			System.out.println(doc);
			String entityName = (String) doc.get("name");
			Bson condition = Filters.eq("_id", doc.get("_id"));
			Document update;
			int randomSmallInt = (int) (Math.random() * (20 - 0 + 1) + 0);
			if (entityName.equals("User") || entityName.equals("Address")) {
				update = new Document("$set", new Document("dataSize", dataSize + randomSmallInt));

				counter++;
				if (counter % 2 == 0)
					dataSize += add;
			} else {
				int max = 5000;
				int min = 3000;
				int random_int = (int) (Math.random() * (max - min + 1) + min);
				update = new Document("$set", new Document("dataSize", random_int));
			}

			coll.updateOne(condition, update);
		}

		System.out.println(counter);
	}

//	public static void main(String[] args) {
//		if (!AnalyticsDB.initConnection(ConsumePostEvents.ANALYTICS_DB_IP, ConsumePostEvents.ANALYTICS_DB_PORT,
//				ConsumePostEvents.ANALYTICS_DB_USER, ConsumePostEvents.ANALYTICS_DB_PWD,
//				ConsumePostEvents.ANALYTICS_DB_NAME))
//			System.exit(1);
//		MongoDatabase db = AnalyticsDB.getDatabase();
//		MongoCollection coll = db.getCollection("TyphonEntityHistory");
//		FindIterable<Document> docs = coll.find();
//		MongoCursor<Document> cursor = docs.iterator();
//
//		while (cursor.hasNext()) {
//			Document doc = cursor.next();
//			Bson condition = Filters.eq("_id", doc.get("_id"));
//			int selects = new RandomDataGenerator().nextInt(0, 50);
//			int updates = new RandomDataGenerator().nextInt(0, 10);
//			int inserts = new RandomDataGenerator().nextInt(0, 10);
//			int deletes = new RandomDataGenerator().nextInt(0, 10);
//			int total = selects + updates + inserts + deletes;
//			Document update = new Document("$set",
//					new BasicDBObject("nbOfSelect", selects).append("nbOfInsert", inserts).append("nbOfUpdate", updates)
//							.append("nbOfDelete", deletes).append("nbOfQueries", total));
//			coll.updateOne(condition, update);
//		}
//
//	}

	public RandomQueryGenerator(TyphonModel model) {
		this.model = model;
	}
	
	public String randomQuery() {

		Random r = new Random();
		int CRUD = r.nextInt(4);

		switch (CRUD) {
		case SELECT:
			return getRandomSelectQuery();
		case UPDATE:
			return getRandomUpdateQuery();
		case DELETE:
			return getRandomDeleteQuery();
		case INSERT:
			return getRandomInsertQuery();
		}

		return null;

	}

	public String getRandomInsertQuery() {
		String res = "insert ";
		List<Entity> entities = model.getEntities();
		int i = new Random().nextInt(entities.size());
		Entity e = entities.get(i);

		res += e.getName() + " {";
		res += getSetClause(e);
		res += "}";
		return res;
	}

	public String getRandomDeleteQuery() {
		List<Entity> entities = model.getEntities();
		int i = new Random().nextInt(entities.size());
		Entity e = entities.get(i);

		List<Entity> mainEntities = new ArrayList<Entity>();
		mainEntities.add(e);
		String res = "delete " + e.getName() + " x0 " + getRandomWhereClause(mainEntities, true);

		return res;
	}

	public String getRandomUpdateQuery() {
		List<Entity> entities = model.getEntities();
		int i = new Random().nextInt(entities.size());
		Entity e = entities.get(i);
		List<Entity> mainEntities = new ArrayList<Entity>();
		mainEntities.add(e);

		String res = "update " + e.getName() + " x0 ";
		if (new Random().nextBoolean()) {
			// where
			res += getRandomWhereClause(mainEntities, true) + " ";
		}

		res += "set {";
		res += getSetClause(e);
		res += "}";

		return res;
	}

	private String getSetClause(Entity e) {
		String res = "";
		EList<EntityAttributeKind> list = e.getAttributes();
		List<Attribute> attributes = new ArrayList<Attribute>();
		for (EntityAttributeKind a : list)
			attributes.add((Attribute) a);
		if (attributes.size() > 0) {
			int nbOfAttrs = new Random().nextInt(attributes.size()) + 1;

			for (int i = 0; i < nbOfAttrs; i++) {
				int index = new Random().nextInt(attributes.size());
				Attribute a = attributes.get(index);

				if (i > 0)
					res += ", ";

				res += a.getName() + ": " + getRandomValue(a);

				attributes.remove(index);
			}

		}

		return res;
	}

	public String getRandomSelectQuery() {
		String res = "from ";
		List<Entity> entities = model.getEntities();

		List<Entity> mainEntities = new ArrayList<Entity>();
		boolean join = new Random().nextBoolean();
		if (join) {

			Entity ent1 = null;
			Entity ent2 = null;
			Relation rel = null;
			while (ent1 == null || ent2 == null) {

				int index1 = new Random().nextInt(entities.size());
				ent1 = entities.get(index1);
				EList<Relation> relations = ent1.getRelations();
				if (relations.size() > 0) {
					int i = new Random().nextInt(relations.size());
					rel = relations.get(i);
					ent2 = rel.getType();
				}

			}

			mainEntities.add(ent1);
			mainEntities.add(ent2);

			res += ent1.getName() + " x0, " + ent2.getName() + " x1";
			res += " select x0, x1 where x0." + rel.getName() + " == x1";

		} else {
			int i = new Random().nextInt(entities.size());
			Entity e = entities.get(i);
			res += e.getName() + " x0 select x0 ";

			mainEntities.add(e);
		}

		boolean where = new Random().nextBoolean();
		if (where) {
			res += getRandomWhereClause(mainEntities, mainEntities.size() == 1);
		}

		return res;
	}

	private String getRandomWhereClause(List<Entity> mainEntities, boolean firstWhereCondition) {
		String res = firstWhereCondition ? "where " : ", ";

		int i = new Random().nextInt(mainEntities.size());
		Entity e = mainEntities.get(i);
		EList<EntityAttributeKind> attrs = e.getAttributes();
		if (attrs.size() == 0)
			return "";

		int i2 = new Random().nextInt(attrs.size());
		EntityAttributeKind a = attrs.get(i2);

		res += "x" + i + "." + a.getName() + " == " + getRandomValue((Attribute) a);

		return res;
	}

	private String getRandomValue(Attribute a) {
		DataType type = a.getType();
		if (type instanceof StringTypeImpl)
			return getRandomStringValue();

		if (type instanceof IntTypeImpl)
			return getRandomIntegerValue();

		if (type instanceof TextTypeImpl)
			return getRandomStringValue();

		if (type instanceof BigintTypeImpl)
			return getRandomIntegerValue();

		if (type instanceof BlobTypeImpl)
			return getRandomBlobValue();

		if (type instanceof BoolTypeImpl)
			return getRandomBooleanValue();

		if (type instanceof DatetimeTypeImpl)
			return getRandomDatetimeValue();

		if (type instanceof FloatTypeImpl)
			return getRandomFloatValue();

		return "\"?\"";
	}

	private String getRandomBlobValue() {
		return getRandomStringValue();
	}

	private String getRandomDatetimeValue() {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		return "$" + date + "$";
	}

	private String getRandomFloatValue() {
		int n1 = new Random().nextInt(1000);
		int n2 = new Random().nextInt(1000);
		return n1 + "." + n2;
	}

	private String getRandomBooleanValue() {
		return new Random().nextBoolean() ? "true" : "false";
	}

	private String getRandomIntegerValue() {
		int n = new Random().nextInt(1000);
		return "" + n;
	}

	private String getRandomStringValue() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = new Random().nextInt(10) + 1;
		Random random = new Random();
		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return "\"" + generatedString + "\"";
	}

}
