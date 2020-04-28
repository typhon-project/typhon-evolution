import {Collection, Cursor, Db} from 'mongodb';
import {MongoCollection} from 'evolution-analytics-model/dist/model/MongoCollection';
import {Model} from 'evolution-analytics-model/dist/model/Model';
import {Entity} from 'evolution-analytics-model/dist/model/Entity';
import {EntityHistory} from 'evolution-analytics-model/dist/model/EntityHistory';

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

    /**
     * Retrieves the polystore schema in the latest version, and builds a JSON array as below:
     * [
     *     {name: RelationalDatabase, type: RELATIONALDB, entities: [{"name":"Product","size":0},{"name":"OrderProduct","size":0},{"name":"User","size":0},{"name":"CreditCard","size":0}]}
     *     {name: 'DocumentDatabase' type: 'DOCUMENTDB', entities: [{"name":"Review","size":0},{"name":"Comment","size":0}]}
     * ]
     * @param db the database containing the polystore schema
     */
    public async getSchema(db: Db) {
        console.log('Get the latest version of the polystore schema');
        let modelCollection: Collection = db.collection(MongoCollection.MODEL_COLLECTION_NAME);
        let entityCollection: Collection = db.collection(MongoCollection.ENTITY_COLLECTION_NAME);
        let entityHistoryCollection: Collection = db.collection(MongoCollection.ENTITY_HISTORY_COLLECTION_NAME);
        //Retrieve the latest version of the model
        this.getModelLatestVersion(modelCollection).then(model => {
            if (model != null) {
                let modelLatestVersion = model.version;
                console.log(`Latest model version: ${modelLatestVersion}`);
                //Retrieve the entities with the latest version of the model
                this.getEntitiesByVersion(entityCollection, modelLatestVersion).then(entities => {
                    if (entities != null) {
                        //Retrieve the entities histories with the latest version of the model
                        this.getEntitiesHistoryByNamesAndVersion(entityHistoryCollection, entities.map(entity => entity.name), modelLatestVersion).then(entitiesHistory => {
                            if (entitiesHistory != null) {
                                //Build the polystore schema
                                return this.buildSchema(entities, entitiesHistory);
                            } else {
                                console.log(`No entities history found for entity names '${entities.map(entity => entity.name)}' and model version: ${modelLatestVersion}`);
                            }
                        }).catch(exception => {
                            console.log(exception);
                        });
                    } else {
                        console.log(`No entities found for model version: ${modelLatestVersion}`);
                    }
                }).catch(exception => {
                    console.log(exception);
                });
            } else {
                console.log(`No model version found in ${modelCollection} collection`);
            }
            return [];
        }).catch(exception => {
            console.log(exception);
        });
    }

    public async getModelLatestVersion(modelCollection: Collection): Promise<Model> {
        if (modelCollection) {
            let model: Cursor<Model> = modelCollection.find<Model>().sort({version: -1}).limit(1);
            if (await model.hasNext()) {
                return await model.next();
            }
        }
        return null;
    }

    public async getEntitiesByVersion(entityCollection: Collection, modelLatestVersion: number) {
        if (entityCollection) {
            let entities: Cursor<Entity> = entityCollection.find<Entity>({'latestVersion': modelLatestVersion});
            if (await entities.hasNext()) {
                return await entities.toArray();
            }
        }
        return null;
    }

    public async getEntitiesHistoryByNamesAndVersion(entityhistoryCollection: Collection, entityNames: string[], modelLatestVersion: number) {
        if (entityhistoryCollection) {
            let entitiesHistory: Cursor<EntityHistory> = entityhistoryCollection.find<EntityHistory>({ name: { $in: entityNames }, 'modelVersion': modelLatestVersion });
            if (await entitiesHistory.hasNext()) {
                return await entitiesHistory.toArray();
            }
        }
        return null;
    }

    private async buildSchema(entities: Entity[], entitiesHistory: EntityHistory[]) {
        if (entities != null && entitiesHistory != null) {
            let dbs = entities
                .filter((entity, index) => {
                    const dbType = entity.dbType;
                    return index === entities.findIndex(obj => {
                        return obj.dbType === dbType;
                    });
                })
                .map(entity => { return { dbName: entity.dbName, dbType: entity.dbType }});
            console.log('Polystore databases:');
            console.log(dbs);
            let schema = [];
            dbs.forEach(db => {
                schema.push({name: db.dbName, type: db.dbType, entities: this.buildSchemaEntities(db, entities, entitiesHistory)});
            });
            console.log('Polystore schema in the latest version:');
            console.log(schema);
            console.log('With entities details:');
            schema.forEach(entity => { console.log(`name: ${entity.name}, type: ${entity.type}, entities: ${JSON.stringify(entity.entities)}`)});
            return schema;
        }
        return null;
    }

    private buildSchemaEntities(db, entities: Entity[], entitiesHistory: EntityHistory[]) {
        return entities.filter((entity) => {
            return entity.dbType === db.dbType
        }).map(entity => {
            return {
                name: entity.name,
                size: entitiesHistory.find(entityHistory => entityHistory.name === entity.name).dataSize
            }
        });
    }
}
