import {MongoService} from "../../services/mongo.service";
import {Request, Response} from "express";
import {MongoHelper} from "../../helpers/mongo.helper";
import {Collection, Db} from "mongodb";

const MONGO_DB_URL = 'mongodb://localhost:27017/test';
const MONGO_DB_NAME = 'test';

export class MongoApiController {

    public static findOne = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.findOne(collection, {id: +request.params.id}).then(documents => {
                if (!documents) {
                    result.status(500).send('No documents found');
                }
                result.status(200).send(documents);

            });
            mongoHelper.disconnect();
        });
    };
    public static findWithFilter = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            const collection: Collection = mongoService.getCollection(db, request.params.collection);
            mongoService.findWithFilter(collection, JSON.stringify(request.body)).then(documents => {
                if (!documents) {
                    result.status(500).send('No documents found');
                }
                result.status(200).send(documents);
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
