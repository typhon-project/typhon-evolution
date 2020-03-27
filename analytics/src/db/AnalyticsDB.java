package db;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteResult;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class AnalyticsDB {
	private static final String DOCUMENT_DB_URL_WITH_AUTH = "mongodb://admin:admin@localhost:27018";
	private static final String DOCUMENT_DB_NAME = "DocumentDatabase";

	private static void injectJSON(File file) throws Throwable {
		MongoClientURI uri = new MongoClientURI(DOCUMENT_DB_URL_WITH_AUTH);
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase(DOCUMENT_DB_NAME);
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();

			String str = new String(data, "UTF-8");
			Document myDoc = Document.parse(str);
			String collectionName = myDoc.keySet().iterator().next();
			MongoCollection<Document> collection = database.getCollection(collectionName);

			List<Document> array = (List<Document>) myDoc.get(collectionName);
			collection.insertMany(array);

		} finally {
			mongoClient.close();
		}

	}

}
