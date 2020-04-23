import {Collection, Db} from 'mongodb';

export class MongoService {
    /*
        Function permitting to find one document from the <MONGO_COLLECTION_NAME> collection
     */
    public findOne = async (collection: Collection, jsonObjectFilter) => {
        return await collection.findOne(jsonObjectFilter)
    };
    /*
        Function permitting to find documents with filter from the <MONGO_COLLECTION_NAME> collection
     */
    public findWithFilter = async (collection: Collection, jsonObjectFilter): Promise<any> => {
        return await collection.find(JSON.parse(jsonObjectFilter)).toArray();
    };
    /*
        Function permitting to find all documents from the <MONGO_COLLECTION_NAME> collection
     */
    public findAll = async (collection: Collection) => {
        return await collection.find({}).toArray();
    };
    /*
        Function permitting to insert ONE json object into the <MONGO_COLLECTION_NAME> collection
     */
    public insertOne = async (collection: Collection, jsonObject) => {
        return await collection.insertOne(JSON.parse(jsonObject));
    };
    /*
        Function permitting to insert ONE json array into the <MONGO_COLLECTION_NAME> collection
     */
    public insertMany = async (collection: Collection, jsonArray) => {
        return await collection.insertMany(JSON.parse(jsonArray));
    };
    /*
        Function permitting to update ONE document with filter in the <MONGO_COLLECTION_NAME> collection
     */
    public updateOneWithFilter = async (collection: Collection, jsonObjectFilter, jsonObjectSet) => {
        return await collection.updateOne(JSON.parse(jsonObjectFilter), {$set: JSON.parse(jsonObjectSet)});
    };
    /*
        Function permitting to delete ONE document with filter from the <MONGO_COLLECTION_NAME> collection
     */
    public deleteOneWithFilter = async (collection: Collection, jsonObjectFilter) => {
        return await collection.deleteOne(JSON.parse(jsonObjectFilter));
    };

    public getCollection(db: Db, collectionName: string): Collection {
        return db.collection(collectionName);
    }
}
