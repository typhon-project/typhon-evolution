import {Router} from 'express';
import {MongoAnalyticsApiController} from "../controllers/mongo.analytics.api.controller";

let mongoAnalyticsApiRouter: Router = Router();

mongoAnalyticsApiRouter.get('/schema', MongoAnalyticsApiController.getSchema);
mongoAnalyticsApiRouter.get('/cruds/:minDate/:maxDate', MongoAnalyticsApiController.getCRUDOperationDistributionByPeriod);
mongoAnalyticsApiRouter.get('/queriedEntities/:minDate/:maxDate', MongoAnalyticsApiController.getQueriedEntitiesProportionByPeriod);
mongoAnalyticsApiRouter.get('/entitiesSize/:minDate/:maxDate', MongoAnalyticsApiController.getEntitiesSizeByPeriod);

mongoAnalyticsApiRouter.get('/cruds2/:minDate/:maxDate/:intervalLength', MongoAnalyticsApiController.getCRUDOperationDistributionByPeriodOverTime);
//mongoAnalyticsApiRouter.get('/queriedEntities2/:minDate/:maxDate', MongoAnalyticsApiController.getQueriedEntitiesProportionByPeriodOverTime);
mongoAnalyticsApiRouter.get('/entitiesSize2/:minDate/:maxDate/:intervalLength', MongoAnalyticsApiController.getEntitiesSizeByPeriodOverTime);
// mongoAnalyticsApiRouter.get('/entities/size/:fromDate/:toDate', MongoAnalyticsApiController.getEntitiesSize);

export {mongoAnalyticsApiRouter};
