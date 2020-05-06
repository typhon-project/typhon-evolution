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

    public static getEntitiesSizeByPeriod = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const minDate = parseInt(request.params.minDate);
        const maxDate = parseInt(request.params.maxDate);

        const sizes = await mongoService.getSchemaByPeriod(db, minDate, maxDate);
        if (sizes != null) {
            console.log('getEntitiesSizeByPeriod successfully executed');
            result.send(sizes);
        } else {
            console.log('Error while getting the entities size');
            result.send('Error while getting the entities size. Check backend logs')
        }
        await mongoHelper.disconnect();
    }

    public static getQueriedEntitiesProportionByPeriod = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const minDate = parseInt(request.params.minDate);
        const maxDate = parseInt(request.params.maxDate);

        const prop = await mongoService.getQueriedEntitiesProportionByPeriod(db, minDate, maxDate);
        if (prop != null) {
            console.log('getQueriedEntitiesProportionByPeriod successfully executed');
            result.send(prop);
        } else {
            console.log('Error while getting the queried entities proportion');
            result.send('Error while getting the queried entities proportion distribution. Check backend logs')
        }
        await mongoHelper.disconnect();
    }

    public static getCRUDOperationDistributionByPeriod = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        const minDate = parseInt(request.params.minDate);
        const maxDate = parseInt(request.params.maxDate);

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

    public static getEntitiesSizeByPeriodOverTime = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        let minDate = parseInt(request.params.minDate);
        let maxDate = parseInt(request.params.maxDate);

        if(minDate === -1 || maxDate === -1) {
            const maxInterval: any[] = await mongoService.getExtremeDates(db);
            console.log('class:'  + maxInterval.constructor.name);
            if(minDate === -1) {
                minDate = maxInterval[0].minDate;
            }
            if(maxDate === -1) {
                maxDate = maxInterval[0].maxDate;
            }
        }

        const intervalSize = parseInt(request.params.intervalLength);

        const intervalMS = MongoAnalyticsApiController.getMillisecondInterval(minDate, maxDate, intervalSize);
        if(intervalMS === 0)
            result.send('Error: bad date interval');

        const sizes = await mongoService.getEntitiesSizeOverTime(db, minDate, maxDate, intervalMS, intervalSize);
        if (sizes != null) {
            console.log('getEntitiesSizeByPeriodOverTime successfully executed');
            result.send(sizes);
        } else {
            console.log('Error while getting the entities size evolution');
            result.send('Error while getting the entities size evolution. Check backend logs')
        }
        await mongoHelper.disconnect();
    };

    public static getCRUDOperationDistributionByPeriodOverTime = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        let minDate = parseInt(request.params.minDate);
        let maxDate = parseInt(request.params.maxDate);

        if(minDate === -1 || maxDate === -1) {
            const maxInterval: any[] = await mongoService.getExtremeDates(db);
            if(minDate === -1) {
                minDate = maxInterval[0].minDate;
            }
            if(maxDate === -1) {
                maxDate = maxInterval[0].maxDate;
            }
        }

        const intervalSize = parseInt(request.params.intervalLength);

        const intervalMS = MongoAnalyticsApiController.getMillisecondInterval(minDate, maxDate, intervalSize);
        if(intervalMS === 0)
            result.send('Error: bad date interval');

        const cruds = await mongoService.getCRUDOperationDistributionByPeriodOverTime(db, minDate, maxDate, intervalMS, intervalSize);
        if (cruds != null) {
            console.log('getCRUDOperationDistributionByPeriod successfully executed');
            result.send(cruds);
        } else {
            console.log('Error while getting the crud operation distribution');
            result.send('Error while getting the crud operation distribution. Check backend logs')
        }
        await mongoHelper.disconnect();
    };

    private static getMillisecondInterval = (minDate: number, maxDate: number, interval: number) => {
        const diff = maxDate - minDate;
        if (diff <= 0)
            return 0;

        return diff / interval;

    }

    // public static getEntitiesSize = (request: Request, result: Response) => {
    // };
}
