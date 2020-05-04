import {Router} from 'express';
import {MongoAnalyticsApiController} from "../controllers/mongo.analytics.api.controller";

let mongoAnalyticsApiRouter: Router = Router();

mongoAnalyticsApiRouter.get('/schema', MongoAnalyticsApiController.getSchema);
mongoAnalyticsApiRouter.get('/cruds/:minDate/:maxDate', MongoAnalyticsApiController.getCRUDOperationDistributionByPeriod);
mongoAnalyticsApiRouter.get('/queriedEntities/:minDate/:maxDate', MongoAnalyticsApiController.getQueriedEntitiesProportionByPeriod);
mongoAnalyticsApiRouter.get('/entitiesSize/:minDate/:maxDate', MongoAnalyticsApiController.getEntitiesSizeByPeriod);

// mongoAnalyticsApiRouter.get('/entities/size/:fromDate/:toDate', MongoAnalyticsApiController.getEntitiesSize);

export {mongoAnalyticsApiRouter};
