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
const MONGO_DB_USERNAME = process.env.MONGO_DB_USERNAME;
const MONGO_DB_PWD = process.env.MONGO_DB_PWD;

export class MongoAnalyticsApiController {

    public static getSchema = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const schema = await mongoService.getSchema(db);
        if (schema != null) {
            console.log('getSchema successfully executed');
            result.send(schema);
        } else {
            console.log('Error while getting the polystore schema');
            result.send('Error while getting the polystore schema. Check backend logs')
        }
        await mongoHelper.disconnect();
    };

    public static getCRUDOperationDistributionByPeriod = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const minDate = parseInt(request.params.minDate);
        const maxDate = parseInt(request.params.maxDate);

        console.log('minDate:' + minDate + '=>' + maxDate);

        const cruds = await mongoService.getCRUDOperationDistributionByPeriod(db, minDate, maxDate);
        if (cruds != null) {
            console.log('getCRUDOperationDistributionByPeriod successfully executed');
            result.send(cruds);
        } else {
            console.log('Error while getting the crud operation distribution');
            result.send('Error while getting the crud operation distribution. Check backend logs')
        }
        await mongoHelper.disconnect();
    };

    // public static getEntitiesSize = (request: Request, result: Response) => {
    // };
}
