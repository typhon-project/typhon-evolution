import {Router} from 'express';
import {MongoCrudApiController} from '../controllers/mongo.crud.api.controller';

let mongoCrudApiRouter: Router = Router();

mongoCrudApiRouter.get('/find/:collection/:id', MongoCrudApiController.findOne);
mongoCrudApiRouter.post('/find/:collection', MongoCrudApiController.findWithFilter);//json object in the body
mongoCrudApiRouter.get('/find/:collection', MongoCrudApiController.findAll);
mongoCrudApiRouter.post('/insert/:collection', MongoCrudApiController.insertOne); //json object in the body
mongoCrudApiRouter.post('/insert/many/:collection', MongoCrudApiController.insertMany); //json array in the body
mongoCrudApiRouter.put('/update/:collection', MongoCrudApiController.updateOne); //json object containing the 'filter' and the 'document' in the body
mongoCrudApiRouter.put('/delete/:collection', MongoCrudApiController.deleteOne); //json object containing the 'filter' in the body

export {mongoCrudApiRouter};
