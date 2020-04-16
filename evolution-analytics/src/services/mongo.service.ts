import {Collection, Db, MongoError} from "mongodb";
import {AnalyticsModel} from "../models/analytics.model";

export class MongoService {
    /*
        Function permitting to insert ONE json object into the <MONGO_COLLECTION_NAME> collection
     */
    public insertOne = async (collection: Collection, jsonObject: AnalyticsModel) => {
        console.log(`insertOne in collection: ${collection.collectionName}`);
        console.log(`content: ${jsonObject.toString()}`);
        return new Promise<any>(
            (
                resolve: (docs: any) => void,
                reject: (err: MongoError) => void
            ) => {
                collection.insertOne(jsonObject, function (err, res) {
                    if (err) {
                        reject(err);
                    }
                    console.log("Inserted one document in the collection");
                    console.log(res.result);
                    resolve(res.result);
                });
            });
    };
    /*
        Function permitting to insert ONE json array into the <MONGO_COLLECTION_NAME> collection
     */
    public insertManyDocuments = async (collection: Collection, jsonArray: AnalyticsModel[]) => {
        console.log(`insertManyDocuments in collection: ${collection.collectionName}`);
        console.log(`content: ${jsonArray.toString()}`);
        return new Promise<any>(
            (
                resolve: (docs: any) => void,
                reject: (err: MongoError) => void
            ) => {
                collection.insertMany(jsonArray, function (err, res) {
                    if (err) {
                        reject(err);
                    }
                    console.log("Inserted documents in the collection");
                    console.log(res.result);
                    resolve(res.result);
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
                    console.log("Found all documents from the collection");
                    if (docs) {
                        for (let doc in docs) {
                            console.log(doc.toString());
                        }
                    }
                    resolve(docs);
                });
            });
    };
    /*
        Function permitting to find documents with filter from the <MONGO_COLLECTION_NAME> collection
     */
    public findDocumentsWithFilter = async (collection: Collection, jsonObjectFilter): Promise<any> => {
        console.log(`findDocumentsWithFilter: ${jsonObjectFilter} in collection: ${collection.collectionName}`);
        return new Promise<AnalyticsModel[]>(
            (
                resolve: (docs: AnalyticsModel[]) => void,
                reject: (err: MongoError) => void
            ) => {
                collection.find(jsonObjectFilter).toArray(function (err, docs) {
                    if (err) {
                        reject(err);
                    }
                    console.log("Found documents with filter from the collection");
                    if (docs) {
                        for (let doc in docs) {
                            console.log(doc.toString());
                        }
                    }
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
                    console.log("Update one document with filter in the collection");
                    console.log(res.result);
                    resolve(res.result);
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
                    console.log("Deleted one document with filter in the collection");
                    console.log(res.result);
                    resolve(res.result);
                });
            });
    };

    public getCollection(db: Db, collectionName: string): Collection {
        return db.collection(collectionName);
    }
}
