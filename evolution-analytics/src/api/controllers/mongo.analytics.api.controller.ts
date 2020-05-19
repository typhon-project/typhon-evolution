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

        const prop = await mongoService.getQueriedEntitiesProportionByPeriod(db, null, minDate, maxDate);
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

        const cruds = await mongoService.getCRUDOperationDistributionByPeriod(db, null, minDate, maxDate);
        if (cruds != null) {
            console.log('getCRUDOperationDistributionByPeriod successfully executed');
            result.send(cruds);
        } else {
            console.log('Error while getting the crud operation distribution');
            result.send('Error while getting the crud operation distribution. Check backend logs')
        }
        await mongoHelper.disconnect();
    };


    public static getQueriedEntitiesProportionByPeriodOverTime = async (request: Request, result: Response) => {
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
        const entityName = request.params.entityName;

        const intervalMS = MongoAnalyticsApiController.getMillisecondInterval(minDate, maxDate, intervalSize);
        if(intervalMS === 0)
            result.send('Error: bad date interval');

        const sizes = await mongoService.getQueriedEntitiesProportionOverTime(db, entityName, minDate, maxDate, intervalMS, intervalSize);
        if (sizes != null) {
            console.log('getQueriedEntitiesProportionByPeriodOverTime successfully executed');
            result.send(sizes);
        } else {
            console.log('Error while getting the queried entities proportion evolution');
            result.send('Error while getting the queried entities proportion evolution. Check backend logs')
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
        const entityName = request.params.entityName;

        const intervalMS = MongoAnalyticsApiController.getMillisecondInterval(minDate, maxDate, intervalSize);
        if(intervalMS === 0)
            result.send('Error: bad date interval');

        const sizes = await mongoService.getEntitiesSizeOverTime(db, entityName, minDate, maxDate, intervalMS, intervalSize);
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
        const entityName = request.params.entityName;

        const intervalMS = MongoAnalyticsApiController.getMillisecondInterval(minDate, maxDate, intervalSize);
        if(intervalMS === 0)
            result.send('Error: bad date interval');

        const cruds = await mongoService.getCRUDOperationDistributionByPeriodOverTime(db, entityName, minDate, maxDate, intervalMS, intervalSize);
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

    public static getSlowestQueries = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        let minDate = parseInt(request.params.minDate);
        let maxDate = parseInt(request.params.maxDate);

        const entityName = request.params.entityName;
        const queries = await mongoService.getSlowestQueries(db, entityName, minDate, maxDate);
        if (queries != null) {
            console.log('getSlowestQueries successfully executed');
            result.send(queries);
        } else {
            console.log('Error while getting the slowest queries');
            result.send('Error while getting the slowest queries. Check backend logs')
        }
        await mongoHelper.disconnect();
    };

    public static getMostFrequentQueries = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        let minDate = parseInt(request.params.minDate);
        let maxDate = parseInt(request.params.maxDate);

        const entityName = request.params.entityName;

        const queries = await mongoService.getMostFrequentQueries(db, entityName, minDate, maxDate);
        if (queries != null) {
            console.log('getMostFrequentQueries successfully executed');
            result.send(queries);
        } else {
            console.log('Error while getting the most frequent queries');
            result.send('Error while getting the most frequent queries. Check backend logs')
        }
        await mongoHelper.disconnect();
    };

    public static getNormalizedQuery = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();

        const queryUUID = request.params.queryUUID;
        console.log('query: ' + queryUUID);
        const queries = await mongoService.getNormalizedQuery(db, queryUUID);
        if (queries != null) {
            console.log('getNormalizedQuery successfully executed');
            result.send(queries);
        } else {
            console.log('Error while getting the normalized query');
            result.send('Error while getting the normalized query. Check backend logs')
        }
        await mongoHelper.disconnect();
    };

    public static getNormalizedQueryEvolution = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        let minDate = parseInt(request.params.minDate);
        let maxDate = parseInt(request.params.maxDate);

        const queryUUID = request.params.normalizedQueryUUID;
        console.log('Normalized query uuid:' + queryUUID)

        const executionTimes = await mongoService.getNormalizedQueryExecutionTimeEvolution(db, queryUUID, minDate, maxDate);
        if (executionTimes != null) {
            console.log('getQueryEvolution successfully executed');
            result.send(executionTimes);
        } else {
            console.log('Error while getting the query execution time evolution');
            result.send('Error while getting the query execution time evolution. Check backend logs')
        }
        await mongoHelper.disconnect();
    };

    public static getNormalizedQueryUUID = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();

        const queryUUID = request.params.queryUUID;

        const normalizedQueryUUID = await mongoService.getNormalizedQueryUUID(db, queryUUID);

        if (normalizedQueryUUID != null) {
            console.log('getNormalizedQueryUUID successfully executed');
            result.send(normalizedQueryUUID);
        } else {
            console.log('Error while getting the normalized query id');
            result.send('Error while getting the normalized query id. Check backend logs')
        }


        await mongoHelper.disconnect();
    };


    public static getQueryEvolution = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();
        let minDate = parseInt(request.params.minDate);
        let maxDate = parseInt(request.params.maxDate);

        const queryUUID = request.params.queryUUID;

        const executionTimes = await mongoService.getQueryExecutionTimeEvolution(db, queryUUID, minDate, maxDate);
        if (executionTimes != null) {
            console.log('getQueryEvolution successfully executed');
            result.send(executionTimes);
        } else {
            console.log('Error while getting the query execution time evolution');
            result.send('Error while getting the query execution time evolution. Check backend logs')
        }
        await mongoHelper.disconnect();
    };

    public static getLatestExecutedQuery = async (request: Request, result: Response) => {
        const mongoHelper = new MongoHelper();
        await mongoHelper.connect(MONGO_DB_URL, MONGO_DB_USERNAME, MONGO_DB_PWD);
        const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
        const mongoService = new MongoService();

        const queryUUID = request.params.normalizedQueryUUID;

        const query = await mongoService.getLatestExecutedQuery(db, queryUUID);
        if (query != null) {
            console.log('getLatestExecutedQuery successfully executed');
            result.send(query);
        } else {
            console.log('Error while getting the latest executed query');
            result.send('Error while getting the latest executed query. Check backend logs')
        }
        await mongoHelper.disconnect();
    };

    // public static getEntitiesSize = (request: Request, result: Response) => {
    // };
}
