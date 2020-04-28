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
const MONGO_DB_USERNAME = process.env.MONGO_DB_USERNAME;
const MONGO_DB_PWD = process.env.MONGO_DB_PWD;

export class MongoCrudApiController {

    public static findOne = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const collection: Collection = mongoService.getCollection(db, request.params.collection);
        const documents = await mongoService.findOne(collection, {_id: new ObjectID(request.params.id)});
        if (documents != null) {
            console.log(`findOne from ${collection.collectionName} collection, results:`);
            console.log(documents);
            result.send(documents);
        } else {
            console.log('No documents found');
            result.send('No document found')
        }
        await mongoHelper.disconnect();
    };

    public static findWithFilter = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const collection: Collection = mongoService.getCollection(db, request.params.collection);
        const documents = await mongoService.findWithFilter(collection, JSON.stringify(request.body));
        if (documents != null) {
            console.log(`findWithFilter from ${collection.collectionName} collection, results:`);
            console.log(documents);
            result.send(documents);
        } else {
            console.log('No documents found');
            result.send('No documents found')
        }
        await mongoHelper.disconnect();
    };

    public static findAll = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const collection: Collection = mongoService.getCollection(db, request.params.collection);
        const documents = await mongoService.findAll(collection);
        if (documents != null) {
            console.log(`findAll from ${collection.collectionName} collection, ${documents.length} documents found`);
            result.send(documents);
        } else {
            console.log('No documents found');
            result.send('No documents found')
        }
        await mongoHelper.disconnect();
    };

    public static insertOne = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const collection: Collection = mongoService.getCollection(db, request.params.collection);
        const res = await mongoService.insertOne(collection, JSON.stringify(request.body));
        if (res.result.ok === 1) {
            console.log(`insertOne result:`);
            console.log(res.ops[0]);
            result.send(res.ops[0]);
        } else {
            console.log(`insertOne error: ${res}`);
            result.send(res);
        }
        await mongoHelper.disconnect();
    };

    public static insertMany = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const collection: Collection = mongoService.getCollection(db, request.params.collection);
        const res = await mongoService.insertMany(collection, JSON.stringify(request.body));
        if (res.result.ok === 1) {
            console.log(`insertMany result:`);
            console.log(res.ops);
            result.send(res.ops);
        } else {
            console.log(`insertMany error: ${res}`);
            result.send(res);
        }
        await mongoHelper.disconnect();
    };

    public static updateOne = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const collection: Collection = mongoService.getCollection(db, request.params.collection);
        const res = await mongoService.updateOneWithFilter(collection, JSON.stringify(request.body.filter), JSON.stringify(request.body.document));
        if (res.result.ok === 1) {
            console.log(`updateOneWithFilter number of updated documents:`);
            console.log(res.modifiedCount);
        } else {
            console.log(`updateOneWithFilter error: ${res}`);
        }
        result.send(res);
        await mongoHelper.disconnect();
    };

    public static deleteOne = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const collection: Collection = mongoService.getCollection(db, request.params.collection);
        const res = await mongoService.deleteOneWithFilter(collection, JSON.stringify(request.body));
        if (res.result.ok === 1) {
            console.log(`deleteOneWithFilter number of deleted documents:`);
            console.log(res.deletedCount);
        } else {
            console.log(`deleteOneWithFilter error: ${res}`);
        }
        result.send(res);
        await mongoHelper.disconnect();
    };
}
