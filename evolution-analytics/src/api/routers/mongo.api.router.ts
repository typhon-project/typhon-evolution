import express from "express";
import { MongoApiController } from "../controllers/mongo.api.controller";

let mongoApiRouter: express.Router = express.Router();

mongoApiRouter.get('/find/:collection/:id', MongoApiController.findOne);
mongoApiRouter.post('/find/:collection', MongoApiController.findWithFilter);//json object in the body
mongoApiRouter.get('/find/:collection', MongoApiController.findAll);
mongoApiRouter.post('/insert/:collection', MongoApiController.insertOne); //json object in the body
mongoApiRouter.post('/insert/many/:collection', MongoApiController.insertMany); //json array in the body
mongoApiRouter.put('/update/:collection', MongoApiController.updateOne); //json object containing the 'filter' and the 'document' in the body
mongoApiRouter.delete('/delete/:collection', MongoApiController.deleteOne); //json object containing the 'filter' in the body

export {mongoApiRouter};
