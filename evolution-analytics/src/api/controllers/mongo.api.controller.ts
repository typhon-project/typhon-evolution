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
                if (!documents) {
                    result.status(500).send('No documents found');
                } else {
                    result.status(200).send(documents);
                }
            }).catch(exception => {
                console.error(exception);
                result.status(500).send(exception);
            });
            mongoHelper.disconnect();
        }).catch(exception => {
            console.error(exception)
        });
    };
    public static findWithFilter = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.findWithFilter(collection, JSON.stringify(request.body)).then(documents => {
                console.log(documents);
                if (!documents) {
                    result.status(500).send('No documents found');
                } else {
                    result.status(200).send(documents);
                }
            }).catch(exception => {
                console.error(exception);
                result.status(500).send(exception);
            });
            mongoHelper.disconnect();
        });
    };
    public static findAll = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.findAll(collection).then(documents => {
                if (!documents) {
                    result.status(500).send('No documents found');
                }
                result.status(200).send(documents);
            });
            mongoHelper.disconnect();
        });
    };
    public static insertOne = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.insertOne(collection, JSON.stringify(request.body)).then(res => {
                result.status(200).send(res);
            });
            mongoHelper.disconnect();
        });
    };
    public static insertMany = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.insertManyDocuments(collection, JSON.stringify(request.body)).then(res => {
                result.status(200).send(res);
            });
            mongoHelper.disconnect();
        });
    };
    public static updateOne = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.updateOneWithFilter(collection, request.body.filter, request.body.document).then(res => {
                result.status(200).send(res);
            });
        });
        mongoHelper.disconnect();
    };
    public static deleteOne = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.deleteOneWithFilter(collection, JSON.stringify(request.body)).then(res => {
                result.status(200).send(res);
            });
            mongoHelper.disconnect();
        });
    };
}
