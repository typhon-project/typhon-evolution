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
     * @deprecated
     * Retrieves the polystore schema in the latest version, and builds a JSON array as below:
     * [
     *     {name: RelationalDatabase, type: RELATIONALDB, entities: [{"name":"Product","size":0},{"name":"OrderProduct","size":0},{"name":"User","size":0},{"name":"CreditCard","size":0}]}
     *     {name: 'DocumentDatabase' type: 'DOCUMENTDB', entities: [{"name":"Review","size":0},{"name":"Comment","size":0}]}
     * ]
     * @param db the database containing the polystore schema
     */
    // public async getSchema(db: Db) {
    //     console.log('Get the latest version of the polystore schema');
    //     const modelCollection: Collection = db.collection(MongoCollection.MODEL_COLLECTION_NAME);
    //     const entityCollection: Collection = db.collection(MongoCollection.ENTITY_COLLECTION_NAME);
    //     const entityHistoryCollection: Collection = db.collection(MongoCollection.ENTITY_HISTORY_COLLECTION_NAME);
    //     //Retrieve the latest version of the model
    //     const model: Model = await this.getModelLatestVersion(modelCollection);
    //     if (model != null) {
    //         const modelLatestVersion = model.version;
    //         console.log(`Latest model version: ${modelLatestVersion}`);
    //         //Retrieve the entities with the latest version of the model
    //         const entities: Entity[] = await this.getEntitiesByVersion(entityCollection, modelLatestVersion);
    //         if (entities != null) {
    //             //Retrieve the entities histories with the latest version of the model
    //             const entitiesHistory: EntityHistory[] = await this.getEntitiesHistoryByNamesAndVersion(entityHistoryCollection, entities.map(entity => entity.name), modelLatestVersion);
    //             if (entitiesHistory != null) {
    //                 //Build the polystore schema
    //                 return this.buildSchema(entities, entitiesHistory);
    //             } else {
    //                 console.log(`No entities history found for entity names '${entities.map(entity => entity.name)}' and model version: ${modelLatestVersion}`);
    //             }
    //         } else {
    //             console.log(`No entities found for model version: ${modelLatestVersion}`);
    //         }
    //     } else {
    //         console.log(`No model version found in ${modelCollection} collection`);
    //     }
    //     return null;
    // }

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
        const modelCollection: Collection = db.collection(MongoCollection.MODEL_COLLECTION_NAME);
        //Retrieve the latest version of the model
        let model: Model = await this.getModelLatestVersion(modelCollection);
        if (model != null) {
            const modelLatestVersion = model.version;
            console.log(`Latest model version: ${modelLatestVersion}`);
            //Retrieve the latest version of the entities and their history
            const databasesEntities = await this.getDatabasesEntitiesByVersion(db, modelLatestVersion);
            //Build the polystore schema
            return this.buildSchema(databasesEntities);
        } else {
            console.log(`No model version found in ${modelCollection} collection`);
        }
        return null;
    }

    public async getSchemaByPeriod(db: Db, minDate: number, maxDate: number) {
        console.log('Get the latest version of the polystore schema');
        const modelCollection: Collection = db.collection(MongoCollection.MODEL_COLLECTION_NAME);
        //Retrieve the latest version of the model
        const model: Model = await this.getModelVersion(modelCollection, minDate, maxDate);
        if (model != null) {
            const modelLatestVersion = model.version;
            console.log(`Model version: ${modelLatestVersion}`);
            //Retrieve the latest version of the entities and their history
            const databasesEntities = await this.getSchemaByPeriod_(db, modelLatestVersion, minDate, maxDate);
            //Build the polystore schema
            return this.buildSchema(databasesEntities);
        } else {
            console.log(`No model version found in ${modelCollection} collection`);
        }
        return null;
    }

    public async getModelLatestVersion(modelCollection: Collection): Promise<Model> {
        if (modelCollection) {
            let model: Cursor<Model> = modelCollection.find<Model>({}).sort({version: -1}).limit(1);
            if (await model.hasNext()) {
                return await model.next();
            }
        }
        return null;
    }

    public async getModelVersion(modelCollection: Collection, minDate: number, maxDate: number): Promise<Model> {
        if (modelCollection) {
            let model: Cursor<Model> = modelCollection.find<Model>( {date: {$lte: maxDate}})
                .sort({version: -1}).limit(1);
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
            let entitiesHistory: Cursor<EntityHistory> = entityhistoryCollection.find<EntityHistory>({
                name: {$in: entityNames},
                'modelVersion': modelLatestVersion
            });
            if (await entitiesHistory.hasNext()) {
                return await entitiesHistory.toArray();
            }
        }
        return null;
    }

    public async getQueriedEntitiesProportionOverTime(db, entityName: string, minDate: number, maxDate: number, msInterval: number, intervalLength: number) {

        let minBound = minDate;
        let maxBound = minDate;
        let i = 0;
        let map = new Map();
        const dates = [];
        while (i < (intervalLength + 1)) {
            const queriedEntities: any[] = await this.getQueriedEntitiesProportionByPeriod(db, entityName, minBound, maxBound);

            for (const entity of queriedEntities) {
                const entityName = entity._id;
                const nbOfQueries = entity.nbOfQueries;

                let history = map.get(entityName);
                if(!history || history == null) {
                    history = [];
                    map.set(entityName, history);
                }

                history.push({date: maxBound, value: nbOfQueries});
            }

            dates.push(maxBound);

            minBound = maxBound + 1;
            maxBound += msInterval;
            i++;
        }

        const res = {dates: dates, entities: []};
        for (const [key, value] of map.entries()) {
            const values: number[] = this.fillArrayWithNullValuesIfNeeded(dates, value);
            res.entities.push({entityName: key, history: values});
        }

        return res;
    }

    private fillArrayWithNullValuesIfNeeded(dates: number[], history: any[]) {
        const res = [];

        let i = 0;
        let j = 0;

        while ( i < dates.length && j < history.length) {
            const date1 = dates[i];
            const date2 = history[j].date;
            if(date1 !== date2) {
                res.push(0);
            } else {
                res.push(history[j].value);
                j++;
            }

            i++;
        }

        while( i < dates.length) {
            res.push(0);
            i++;
        }

        return res;
    }


    public async  getEntitiesSizeOverTime(db, entityName: string, minDate: number, maxDate: number, msInterval: number, intervalLength: number) {
        let minBound = minDate;
        let maxBound = minDate;
        let i = 0;
        let map = new Map();
        const dates = [];
        console.log('interval length:' + intervalLength);
        while (i < (intervalLength + 1)) {
            const sizes: any[] = await this.getEntitiesSizeByPeriod(db, entityName, minBound, maxBound);


            for( const size of sizes) {
                const entityName = size._id;
                const entitySize = size.size;

                let history = map.get(entityName);
                if(!history || history == null) {
                    history = [];
                    map.set(entityName, history);
                }

                history.push({date: maxBound, value: entitySize});
            }

            dates.push(maxBound);

            minBound = maxBound + 1;
            maxBound += msInterval;
            i++;
        }

        const res = {dates: dates, entities: []};
        for (const [key, value] of map.entries()) {
            const values: number[] = this.fillArrayWithNullValuesIfNeeded(dates, value);
            res.entities.push({entityName: key, history: values});
        }

        return res;
    }

    private async getEntitySizeByPeriod(db, entityName: string, minDate: number, maxDate: number) {
        const sizes =
            db.collection(MongoCollection.ENTITY_HISTORY_COLLECTION_NAME).aggregate(
                [
                    {
                        $match: {
                            $and: [
                                {name: {$eq: entityName}},
                                {updateDate: {$lte: maxDate}}
                            ]
                        }
                    },
                    { $sort: { "name": 1, "updateDate": -1 } },
                    {
                        $group:
                            {
                                _id: "$name",
                                updateDate: { $first: "$updateDate" },
                                size: {$first: "$dataSize"}
                            }
                    }
                ]
            );
        if (await sizes.hasNext()) {
            return await sizes.toArray();
        } else
            return [];
    }

    private async getEntitiesSizeByPeriod(db, entityName: string, minDate: number, maxDate: number) {

        const sizes =
            db.collection(MongoCollection.ENTITY_HISTORY_COLLECTION_NAME).aggregate(
                [
                    {
                        $match: {
                            $and: [
                                (entityName && entityName != null ? {name: {$eq: entityName}} : {}),
                                {updateDate: {$lte: maxDate}}
                            ]
                        }
                    },
                    { $sort: { "name": 1, "updateDate": -1 } },
                    {
                        $group:
                            {
                                _id: "$name",
                                updateDate: { $first: "$updateDate" },
                                size: {$first: "$dataSize"}
                            }
                    }
                ]
            );
        if (await sizes.hasNext()) {
            return await sizes.toArray();
        } else
            return [];

    }

    public async getQueriedEntitiesProportionByPeriod(db, entityName: string, minDate: number, maxDate: number) {
        const modelCollection: Collection = db.collection(MongoCollection.MODEL_COLLECTION_NAME);
        //Retrieve the latest version of the model
        const model: Model = await this.getModelLatestVersion(modelCollection);
        if (model != null) {
            // return {_id: entityName, nbOfQueries: ?}

            const modelVersion = model.version;
            const prop =
                db.collection(MongoCollection.ENTITY_HISTORY_COLLECTION_NAME).aggregate([
                    {
                        $match: {
                            $and: [
                                (entityName && entityName != null ? {name: {$eq: entityName}} : {}),
                                {updateDate: {$lte: maxDate}},
                                {updateDate: {$gte: minDate}}
                            ]
                        }
                    },
                    {
                        $group: {
                            _id: "$name",
                            nbOfQueries: {$sum: "$nbOfQueries"}
                        }
                    },
                    { $sort: { nbOfQueries: -1 } }]);
            if (await prop.hasNext()) {
                return await prop.toArray();
            } else
                return [];
        }

        return null;
    }

    public async getExtremeDates(db) {

        const res = db.collection(MongoCollection.ENTITY_HISTORY_COLLECTION_NAME).aggregate(
            [
                {
                    $group:
                        {
                            _id: null,
                            minDate: { $min: "$updateDate" },
                            maxDate: { $max: "$updateDate" }
                        }
                }
            ]
        );

        if (await res.hasNext()) {
            return await res.toArray();
        } else
            return [];
    }

    public async getCRUDOperationDistributionByPeriodOverTime(db, entityName: string, minDate: number, maxDate: number, msInterval: number, intervalLength: number) {

        const timeArray = [];
        const valueArray = [];
        let minBound = minDate;
        let maxBound = minDate;
        let i = 0;
        console.log('interval length:' + intervalLength);
        while (i < (intervalLength + 1)) {
            const cruds: any[] = await this.getCRUDOperationDistributionByPeriod(db, entityName, minBound, maxBound);
            let selects = 0;
            let updates = 0;
            let deletes = 0;
            let inserts = 0;
            if(cruds && cruds != null && cruds.length > 0) {
                selects = cruds[0].selects;
                updates = cruds[0].updates;
                deletes = cruds[0].deletes;
                inserts = cruds[0].inserts;
            }

            valueArray.push({selects: selects, updates: updates, deletes: deletes, inserts: inserts});
            timeArray.push(maxBound);
            console.log('push:' + maxBound);

            minBound = maxBound + 1;
            maxBound += msInterval;
            i++;
        }


        return [{time: timeArray, values: valueArray}];
    }

    public async getCRUDOperationDistributionByPeriod(db, entityName: string, minDate: number, maxDate: number) {
        const modelCollection: Collection = db.collection(MongoCollection.MODEL_COLLECTION_NAME);
        //Retrieve the latest version of the model
        const model: Model = await this.getModelLatestVersion(modelCollection);
        if (model != null) {
            // return {_id: null, selects: ?, deletes: ?, updates: ?, inserts: ?}

            const modelVersion = model.version;
            const cruds =
                db.collection(MongoCollection.ENTITY_HISTORY_COLLECTION_NAME).aggregate([
                    {
                        $match: {
                            $and: [
                                (entityName && entityName != null ? {name: {$eq: entityName}} : {}),
                                {modelVersion: modelVersion},
                                {updateDate: {$lte: maxDate}},
                                {updateDate: {$gte: minDate}}
                            ]
                        }
                    },
                    {
                        $group: {
                            _id: null,
                            selects: {$sum: "$nbOfSelect"},
                            deletes: {$sum: "$nbOfDelete"},
                            updates: {$sum: "$nbOfUpdate"},
                            inserts: {$sum: "$nbOfInsert"}
                        }
                    }]);
            if (await cruds.hasNext()) {
                return await cruds.toArray();
            } else
                return [];
        }

        return null;

    }

    public async getSchemaByPeriod_(db, modelLatestVersion: number, minDate: number, maxDate: number) {
        const databaseEntities = db.collection(MongoCollection.ENTITY_COLLECTION_NAME).aggregate([
            {$match: {latestVersion: modelLatestVersion}},
            {$lookup: {
                    from: db.collection(MongoCollection.ENTITY_HISTORY_COLLECTION_NAME).collectionName,
                    let: {entityVersion: '$latestVersion', entityName: '$name'},
                    pipeline: [{
                        $match: {
                            $expr: {
                                $and: [
                                    {$eq: ['$modelVersion', '$$entityVersion']},
                                    {$eq: ['$name', '$$entityName']},
                                    {$lte: ['$updateDate', maxDate]}
                                ]
                            }
                        }
                    },
                        {$sort: {'updateDate': -1}},
                        {$limit: 1},
                        {$project: {'dataSize': 1, 'modelVersion': 1, 'updateDate': 1}}
                    ],
                    as: 'entitieshistories'
                }
            },
            {
                $replaceRoot: {
                    newRoot: {
                        $mergeObjects: [
                            {$arrayElemAt: ["$entitieshistories", 0]}, "$$ROOT"
                        ]
                    }
                }
            },
            {$project: {entitieshistories: 0, versions: 0, modelVersion: 0}},
            {
                $group: {
                    _id: {dbName: '$dbName', dbType: '$dbType'},
                    entities: {$push: "$$ROOT"}
                }
            }
        ]);
        if (await databaseEntities.hasNext()) {
            return await databaseEntities.toArray();
        }
        return null;
    }

    public async getSlowestQueries(db, entityName: string, minDate: number, maxDate: number) {
        const queries =
            db.collection(MongoCollection.QUERY_COLLECTION_NAME).aggregate([
                {$match: {
                        $and: [
                            (entityName && entityName != null ? {allEntities: entityName} : {}),
                            {executionDate: {$lte: maxDate}},
                            {executionDate: {$gte: minDate}}
                        ]
                    }},

                { $sort: { executionTime: -1 } }
            ]);
        if (await queries.hasNext()) {
            return await queries.toArray();
        } else {
            return [];
        }


    }

    public async getMostFrequentQueries(db, entityName: string, minDate: number, maxDate: number) {
        const queries =
                db.collection(MongoCollection.QUERY_COLLECTION_NAME).aggregate([
                    {$match: {
                            $and: [
                                (entityName && entityName != null ? {allEntities: entityName} : {}),
                                {executionDate: {$lte: maxDate}},
                                {executionDate: {$gte: minDate}}
                            ]
                        }},
                    {$lookup: {
                            from: db.collection(MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME).collectionName,
                            localField: 'normalizedQueryId',
                            foreignField: '_id',
                            as: 'normalizedqueries'
                        }
                    },
                    {
                        $replaceRoot: {
                            newRoot: {
                                $mergeObjects: [
                                    {$arrayElemAt: ["$normalizedqueries", 0]}, "$$ROOT"
                                ]
                            }
                        }
                    },
                    {
                        $group: {
                            _id: '$normalizedQueryId',
                            count: {$sum: 1},
                            avgExecutionTime: {$avg: '$executionTime'},
                            query:  {$first: '$displayableForm' }
                        }
                    },
                    { $sort: { count: -1 } }
                ]);
            if (await queries.hasNext()) {
                return await queries.toArray();
            } else {
                return [];
            }


    }

    public async getQueryExecutionTimeEvolution(db, queryUUID: string, minDate: number, maxDate: number) {
        const normalizedQueryUUID = await this.getNormalizedQueryUUID(db, queryUUID);
        if (normalizedQueryUUID.length === 0)
            return [];
        const uuid = normalizedQueryUUID[0].normalizedQueryId;
        return this.getNormalizedQueryExecutionTimeEvolution(db, uuid, minDate, maxDate);
    }

    public async getNormalizedQueryUUID(db, queryUUID: string) {
        var ObjectID = require('mongodb').ObjectID;
        var objectId = new ObjectID(queryUUID);
        const queries =
            db.collection(MongoCollection.QUERY_COLLECTION_NAME).find(
                {_id: objectId})
                .sort({normalizedQueryId: 1});
        if (await queries.hasNext()) {
            return await queries.toArray();
        } else {
            return [];
        }
    }

    public async getLatestExecutedQuery(db, queryUUID: string) {
        var ObjectID = require('mongodb').ObjectID;
        var objectId = new ObjectID(queryUUID);
        const queries =
            db.collection(MongoCollection.QUERY_COLLECTION_NAME).find(
                {normalizedQueryId: objectId})
                .sort({executionDate: -1})
                .limit(1);
        if (await queries.hasNext()) {
            return await queries.toArray();
        } else {
            return [];
        }
    }

    public async getNormalizedQueryExecutionTimeEvolution(db, normalizedQueryUUID: string, minDate: number, maxDate: number) {
       var ObjectID = require('mongodb').ObjectID;
       var objectId = new ObjectID(normalizedQueryUUID);

        const queries =
            db.collection(MongoCollection.QUERY_COLLECTION_NAME).find(
                {normalizedQueryId: objectId, executionDate: {$lte: maxDate, $gte: minDate}},
                {executionDate: 1, executionTime: 1})
                .sort({executionDate: 1});
        if (await queries.hasNext()) {
            return await queries.toArray();
        } else {
            return [];
        }
    }

    public async getNormalizedQuery(db, qlQueryUUID: string) {
        const queries = await this.getNormalizedQueryUUID(db, qlQueryUUID);
        if (!queries || queries.length !== 1)
            return[];
        const normalizedQueryId = queries[0].normalizedQueryId;
        console.log('iic:' + normalizedQueryId);
        var ObjectID = require('mongodb').ObjectID;
        var objectId = new ObjectID(normalizedQueryId);
        const res =
            db.collection(MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME).find(
                {_id: objectId});
        if (await res.hasNext()) {
            return await res.toArray();
        } else {
            return [];
        }

    }


    public async getDatabasesEntitiesByVersion(db, modelLatestVersion: number) {
        const databaseEntities = db.collection(MongoCollection.ENTITY_COLLECTION_NAME).aggregate([
            {$match: {latestVersion: modelLatestVersion}},
            {
                $lookup: {
                    from: db.collection(MongoCollection.ENTITY_HISTORY_COLLECTION_NAME).collectionName,
                    let: {entityVersion: '$latestVersion', entityName: '$name'},
                    pipeline: [{
                        $match: {
                            $expr: {
                                $and: [
                                    {$eq: ['$modelVersion', '$$entityVersion']},
                                    {$eq: ['$name', '$$entityName']}
                                ]
                            }
                        }
                    },
                        {$sort: {'updateDate': -1}},
                        {$limit: 1},
                        {$project: {'dataSize': 1, 'modelVersion': 1, 'updateDate': 1}}
                    ],
                    as: 'entitieshistories'
                }
            },
            {
                $replaceRoot: {
                    newRoot: {
                        $mergeObjects: [
                            {$arrayElemAt: ["$entitieshistories", 0]}, "$$ROOT"
                        ]
                    }
                }
            },
            {$project: {entitieshistories: 0, versions: 0, modelVersion: 0}},
            {
                $group: {
                    _id: {dbName: '$dbName', dbType: '$dbType'},
                    entities: {$push: "$$ROOT"}
                }
            }
        ]);
        if (await databaseEntities.hasNext()) {
            return await databaseEntities.toArray();
        }
        return null;
    }


    private buildSchema(databaseEntities: any[]) {
        if (databaseEntities != null) {
            let schema = [];
            databaseEntities.forEach(db => {
                schema.push({
                    name: db._id.dbName,
                    type: db._id.dbType,
                    entities: db.entities.map(entity => {
                        return {
                            name: entity.name,
                            size: entity.dataSize
                        }
                    })
                });
            });
            console.log('Polystore schema in the latest version:');
            console.log(schema);
            console.log('With entities details:');
            schema.forEach(entity => {
                console.log(`name: ${entity.name}, type: ${entity.type}, entities: ${JSON.stringify(entity.entities)}`)
            });
            return schema;
        }
        return null;
    }

}
