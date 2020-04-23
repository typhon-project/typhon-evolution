import {MongoService} from '../../services/mongo.service';
import {Request, Response} from 'express';
import {MongoHelper} from '../../helpers/mongo.helper';
import {Collection, Db, ObjectID} from 'mongodb';
import {config} from 'dotenv';

//Import .env configuration file
config();

//Retrieve environment variables from .env file
const MONGO_DB_URL = process.env.MONGO_DB_URL;
const MONGO_DB_NAME = process.env.MONGO_DB_NAME;

export class MongoApiController {

    public static findOne = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            console.log(MONGO_DB_URL);
            console.log(MONGO_DB_NAME);
            mongoService.findOne(collection, {_id: new ObjectID(request.params.id)}).then(documents => {
                console.log(documents);
                return result.send(documents);
            }).catch(exception => {
                console.error(exception);
            });
        }).catch(exception => {
            console.error(exception)
        }).finally(function () {
            mongoHelper.disconnect()
        });
    };
    public static findWithFilter = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.findWithFilter(collection, JSON.stringify(request.body)).then(documents => {
                console.log(`findWithFilter from ${collection.collectionName} collection, results:\n${JSON.stringify(documents)}`);
                if (documents) {
                    return result.send(documents);
                } else {
                    return result.send('No documents found');
                }
            }).catch(exception => {
                console.error(exception);
            });
        }).catch(exception => {
            console.error(exception)
        }).finally(function () {
            mongoHelper.disconnect()
        });
    };
    public static findAll = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.findAll(collection).then(documents => {
                console.log(`findAll from ${collection.collectionName} collection: ${documents.length} documents found`);
                if (documents) {
                    return result.send(documents);
                } else {
                    return result.send('No documents found');
                }
            }).catch(exception => {
                console.error(exception);
            });
        }).catch(exception => {
            console.error(exception)
        }).finally(function () {
            mongoHelper.disconnect()
        });
    };
    public static insertOne = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.insertOne(collection, JSON.stringify(request.body)).then(res => {
                console.log(`insertOne in ${collection.collectionName} collection: ${JSON.stringify(request.body)}`);
                if (res.result.ok === 1) {
                    console.log(`insertOne result: ${JSON.stringify(res.ops[0])}`);
                    return result.send(res.ops[0]);
                } else {
                    console.log(`insertOne error: ${res}`);
                    return result.send(res);
                }
            }).catch(exception => {
                console.error(exception);
            });
        }).catch(exception => {
            console.error(exception)
        }).finally(function () {
            mongoHelper.disconnect()
        });
    };
    public static insertMany = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.insertMany(collection, JSON.stringify(request.body)).then(res => {
                console.log(`insertMany in ${collection.collectionName} collection: ${JSON.stringify(request.body)}`);
                if (res.result.ok === 1) {
                    console.log(`insertMany result: ${JSON.stringify(res.ops)}`);
                    return result.send(res.ops);
                } else {
                    console.log(`insertMany error: ${res}`);
                    return result.send(res);
                }
            }).catch(exception => {
                console.error(exception);
            });
        }).catch(exception => {
            console.error(exception)
        }).finally(function () {
            mongoHelper.disconnect()
        });
    };
    public static updateOne = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.updateOneWithFilter(collection, JSON.stringify(request.body.filter), JSON.stringify(request.body.document)).then(res => {
                console.log(
                    `updateOneWithFilter in ${collection.collectionName} collection. ` +
                    `Filter: ${JSON.stringify(request.body.filter)}, update: ${JSON.stringify(request.body.document)}`
                );
                if (res.result.ok === 1) {
                    console.log(`updateOneWithFilter number of updated documents: ${JSON.stringify(res.modifiedCount)}`);
                } else {
                    console.log(`updateOneWithFilter error: ${res}`);
                }
                return result.send(res);
            }).catch(exception => {
                console.error(exception);
            });
        }).catch(exception => {
            console.error(exception)
        }).finally(function () {
            mongoHelper.disconnect()
        });
    };
    public static deleteOne = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.deleteOneWithFilter(collection, JSON.stringify(request.body)).then(res => {
                console.log(`deleteOneWithFilter in ${JSON.stringify(collection.collectionName)} collection: ${JSON.stringify(request.body.filter)}`);
                if (res.result.ok === 1) {
                    console.log(`deleteOneWithFilter number of deleted documents: ${JSON.stringify(res.deletedCount)}`);
                } else {
                    console.log(`deleteOneWithFilter error: ${res}`);
                }
                return result.send(res);
            }).catch(exception => {
                console.error(exception);
            });
        }).catch(exception => {
            console.error(exception)
        }).finally(function () {
            mongoHelper.disconnect()
        });
    };
}
