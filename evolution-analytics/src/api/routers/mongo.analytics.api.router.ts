import {Router} from 'express';
import {MongoAnalyticsApiController} from "../controllers/mongo.analytics.api.controller";

let mongoAnalyticsApiRouter: Router = Router();

mongoAnalyticsApiRouter.get('/schema', MongoAnalyticsApiController.getSchema);
// mongoAnalyticsApiRouter.get('/entities/size/:fromDate/:toDate', MongoAnalyticsApiController.getEntitiesSize);

export {mongoAnalyticsApiRouter};
