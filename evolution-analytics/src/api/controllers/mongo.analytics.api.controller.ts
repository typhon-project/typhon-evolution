import {MongoService} from '../../services/mongo.service';
import {Request, Response} from 'express';
import {MongoHelper} from '../../helpers/mongo.helper';
import {Db} from 'mongodb';
import {config} from 'dotenv';

//Import .env configuration file
config();

//Retrieve environment variables from .env file
const MONGO_DB_URL = process.env.MONGO_DB_URL;
const MONGO_DB_NAME = process.env.MONGO_DB_NAME;

export class MongoAnalyticsApiController {

    public static getSchema = (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        mongoHelper.connect(MONGO_DB_URL).then(() => {
            const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
            const mongoService = new MongoService();
            return result.send(mongoService.getSchema(db));
        }).catch(exception => {
            console.error(exception)
        }).finally(function () {
            mongoHelper.disconnect()
        });
    };
    // public static getEntitiesSize = (request: Request, result: Response) => {
    //     let fromDate: Date = request.params.fromDate;
    //     const mongoHelper = new MongoHelper();
    //     mongoHelper.connect(MONGO_DB_URL).then(() => {
    //         const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
    //         const mongoService = new MongoService();
    //         const collection: Collection = mongoService.getCollection(db, request.params.collection);
    //         mongoService.findWithFilter(collection, JSON.stringify(request.body)).then(documents => {
    //             console.log(`findWithFilter from ${collection.collectionName} collection, results:\n${JSON.stringify(documents)}`);
    //             if (documents) {
    //                 return result.send(documents);
    //             } else {
    //                 return result.send('No documents found');
    //             }
    //         }).catch(exception => {
    //             console.error(exception);
    //         });
    //     }).catch(exception => {
    //         console.error(exception)
    //     }).finally(function () {
    //         mongoHelper.disconnect()
    //     });
    // };
}
