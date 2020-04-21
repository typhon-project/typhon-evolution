import {Collection, Db, MongoError} from 'mongodb';

export class MongoService {
    /*
        Function permitting to insert ONE json object into the <MONGO_COLLECTION_NAME> collection
     */
    public insertOne = async (collection: Collection, jsonObject) => {
        console.log(`insertOne in collection: ${collection.collectionName}`);
        console.log(`content: ${jsonObject}`);
        return new Promise<any>(
            (
                resolve: (docs: any) => void,
                reject: (err: MongoError) => void
            ) => {
                collection.insertOne(jsonObject, function (err, res) {
                    if (err) {
                        reject(err);
                    }
                    console.log(`Inserted one document in '${collection.collectionName}' collection, result: ${res.result}`);
                    resolve(res.result.ok);
                });
            });
    };
    /*
        Function permitting to insert ONE json array into the <MONGO_COLLECTION_NAME> collection
     */
    public insertManyDocuments = async (collection: Collection, jsonArray) => {
        console.log(`insertManyDocuments in collection: ${collection.collectionName}`);
        console.log(`content: ${jsonArray}`);
        return new Promise<any>(
            (
                resolve: (docs: any) => void,
                reject: (err: MongoError) => void
            ) => {
                collection.insertMany(jsonArray, function (err, res) {
                    if (err) {
                        reject(err);
                    }
                    console.log(`Inserted documents in '${collection.collectionName}' collection, result: ${res.result}`);
                    resolve(res.result.ok);
                });
            });
    };
    /*
        Function permitting to find one document from the <MONGO_COLLECTION_NAME> collection
     */
    public findOne = async (collection: Collection, jsonObjectFilter) => {
        console.log(`findOne from collection: ${collection.collectionName}, filter: ${jsonObjectFilter}`);
        return await collection.findOne(jsonObjectFilter)
    };
    /*
        Function permitting to find documents with filter from the <MONGO_COLLECTION_NAME> collection
     */
    public findWithFilter = async (collection: Collection, jsonObjectFilter): Promise<any> => {
        console.log(`findDocumentsWithFilter: ${jsonObjectFilter} in collection: ${collection.collectionName}`);
        return new Promise<any[]>(
            (
                resolve: (docs: any[]) => void,
                reject: (err: MongoError) => void
            ) => {
                collection.find(jsonObjectFilter).toArray(function (err, docs) {
                    if (err) {
                        reject(err);
                    }
                    console.log(`Found documents with filter from '${collection.collectionName}' collection, number: ${docs.length}`);
                    resolve(docs);
                });
            });
    };
    /*
        Function permitting to find all documents from the <MONGO_COLLECTION_NAME> collection
     */
    public findAll = async (collection: Collection) => {
        console.log(`findAll from collection: ${collection.collectionName}`);
        return new Promise<any[]>(
            (
                resolve: (docs: any[]) => void,
                reject: (err: MongoError) => void
            ) => {
                collection.find({}).toArray(function (err, docs) {
                    if (err) {
                        reject(err);
                    }
                    console.log(`Found all documents from '${collection.collectionName}' collection, number: ${docs.length}`);
                    resolve(docs);
                });
            });
    };
    /*
        Function permitting to update ONE document with filter in the <MONGO_COLLECTION_NAME> collection
     */
    public updateOneWithFilter = async (collection: Collection, jsonObjectFilter, jsonObjectSet) => {
        console.log(`updateOneWithFilter: ${jsonObjectFilter} in collection: ${collection.collectionName}`);
        console.log(`update: {$set: ${jsonObjectSet}}`);
        return new Promise<any>(
            (
                resolve: (result: any) => void,
                reject: (err: MongoError) => void
            ) => {
                collection.updateOne(jsonObjectFilter, {$set: jsonObjectSet}, function (err, res) {
                    if (err) {
                        reject(err);
                    }
                    console.log(`Update one document with filter in '${collection.collectionName}' collection, result: ${res.result}`);
                    resolve(res.result.ok);
                });
            });
    };
    /*
        Function permitting to delete ONE document with filter from the <MONGO_COLLECTION_NAME> collection
     */
    public deleteOneWithFilter = async (collection: Collection, jsonObjectFilter) => {
        console.log(`deleteOneWithFilter: ${jsonObjectFilter} from collection: ${collection.collectionName}`);
        return new Promise<any>(
            (
                resolve: (result: any) => void,
                reject: (err: MongoError) => void
            ) => {
                collection.deleteOne(jsonObjectFilter, function (err, res) {
                    if (err) {
                        reject(err);
                    }
                    console.log(`Deleted one document with filter in '${collection.collectionName}' collection, result: ${res.result}`);
                    resolve(res.result.ok);
                });
            });
    };

    public getCollection(db: Db, collectionName: string): Collection {
        return db.collection(collectionName);
    }
}
