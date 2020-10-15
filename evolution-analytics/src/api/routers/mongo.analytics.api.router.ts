import {Router} from 'express';
import {MongoAnalyticsApiController} from "../controllers/mongo.analytics.api.controller";

let mongoAnalyticsApiRouter: Router = Router();

mongoAnalyticsApiRouter.get('/schema', MongoAnalyticsApiController.getSchema);
mongoAnalyticsApiRouter.get('/cruds/:minDate/:maxDate', MongoAnalyticsApiController.getCRUDOperationDistributionByPeriod);
mongoAnalyticsApiRouter.get('/queriedEntities/:minDate/:maxDate', MongoAnalyticsApiController.getQueriedEntitiesProportionByPeriod);
mongoAnalyticsApiRouter.get('/entitiesSize/:minDate/:maxDate', MongoAnalyticsApiController.getEntitiesSizeByPeriod);

mongoAnalyticsApiRouter.get('/cruds2/:minDate/:maxDate/:intervalLength', MongoAnalyticsApiController.getCRUDOperationDistributionByPeriodOverTime);
mongoAnalyticsApiRouter.get('/cruds2/:minDate/:maxDate/:intervalLength/:entityName', MongoAnalyticsApiController.getCRUDOperationDistributionByPeriodOverTime);

mongoAnalyticsApiRouter.get('/queriedEntities2/:minDate/:maxDate/:intervalLength', MongoAnalyticsApiController.getQueriedEntitiesProportionByPeriodOverTime);
mongoAnalyticsApiRouter.get('/queriedEntities2/:minDate/:maxDate/:intervalLength/:entityName', MongoAnalyticsApiController.getQueriedEntitiesProportionByPeriodOverTime);
mongoAnalyticsApiRouter.get('/entitiesSize2/:minDate/:maxDate/:intervalLength', MongoAnalyticsApiController.getEntitiesSizeByPeriodOverTime);
mongoAnalyticsApiRouter.get('/entitiesSize2/:minDate/:maxDate/:intervalLength/:entityName', MongoAnalyticsApiController.getEntitiesSizeByPeriodOverTime);


// mongoAnalyticsApiRouter.get('/cruds2Entity/:entityName/:minDate/:maxDate/:intervalLength', MongoAnalyticsApiController.getCRUDOperationDistributionPerformedOnGivenEntityOverTime);

// mongoAnalyticsApiRouter.get('/entities/size/:fromDate/:toDate', MongoAnalyticsApiController.getEntitiesSize);

mongoAnalyticsApiRouter.get('/mostFrequentQueries/:minDate/:maxDate', MongoAnalyticsApiController.getMostFrequentQueries);
mongoAnalyticsApiRouter.get('/mostFrequentQueries/:minDate/:maxDate/:entityName', MongoAnalyticsApiController.getMostFrequentQueries);
mongoAnalyticsApiRouter.get('/slowestQueries/:minDate/:maxDate', MongoAnalyticsApiController.getSlowestQueries);
mongoAnalyticsApiRouter.get('/slowestQueries/:minDate/:maxDate/:entityName', MongoAnalyticsApiController.getSlowestQueries);

mongoAnalyticsApiRouter.get('/query/:minDate/:maxDate/:queryUUID', MongoAnalyticsApiController.getQueryEvolution);
mongoAnalyticsApiRouter.get('/normalizedQuery/:minDate/:maxDate/:normalizedQueryUUID', MongoAnalyticsApiController.getNormalizedQueryEvolution);
mongoAnalyticsApiRouter.get('/normalizedQuery/:queryUUID', MongoAnalyticsApiController.getNormalizedQuery);


mongoAnalyticsApiRouter.get('/latestQuery/:normalizedQueryUUID', MongoAnalyticsApiController.getLatestExecutedQuery);

mongoAnalyticsApiRouter.get('/normalizedQueryId/:queryUUID', MongoAnalyticsApiController.getNormalizedQueryUUID);

mongoAnalyticsApiRouter.get('/recommendations/:normalizedQueryUUID', MongoAnalyticsApiController.recommend);
mongoAnalyticsApiRouter.get('/evolve/:changeOperator', MongoAnalyticsApiController.evolve);

export {mongoAnalyticsApiRouter};
