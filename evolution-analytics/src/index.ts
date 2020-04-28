import express from 'express';
import SocketIOStatic from 'socket.io';
import {MongoHelper} from './helpers/mongo.helper';
import {MongoService} from './services/mongo.service';
import {SocketService} from './services/socket.service';
import {mongoCrudApiRouter} from './api/routers/mongo.crud.api.router';
import {mongoAnalyticsApiRouter} from "./api/routers/mongo.analytics.api.router";
import {config} from 'dotenv';
import {Db} from 'mongodb';
import {createServer} from 'http';
import e from "cors";


//Import .env configuration file
config();

const app = express();
app.use(express.json());
app.use(express.urlencoded({extended: true}));
//Cors configuration for access control for webservices: authorize all clients to use the webservices, all methods (get, put, ...), headers, ...
app.use(e());
//API routers
app.use('/', mongoCrudApiRouter);
app.use('/', mongoAnalyticsApiRouter);

const httpServer = createServer(app);
const io = SocketIOStatic(httpServer);

const mongoService = new MongoService();
const mongoHelper = new MongoHelper();
const socketService = new SocketService();

//Retrieve environment variables from .env file
const MONGO_DB_URL = process.env.MONGO_DB_URL;
const MONGO_DB_NAME = process.env.MONGO_DB_NAME;
const PORT = process.env.SERVER_PORT;

mongoHelper.connect(MONGO_DB_URL).then(async () => {
    const db: Db = mongoHelper.client.db(MONGO_DB_NAME);
    // const collection: mongodb.Collection = mongoService.getCollection(db, MONGO_COLLECTION_NAME);
    //mongoService.insertOne(collection, {id: 1, query: 'from User u, Order o select u.orders where u.id = ?'});
    //mongoService.insertManyDocuments(collection, [{id: 2, query: 'from User u, Order o select o where o.quantity = ?'}]);
    //mongoService.findAll(collection);
    //mongoService.findDocumentsWithFilter(collection, {'id': 1});
    //mongoService.updateOneWithFilter(collection, {'id': 1}, {query: 'from User u, Order o select u.orders where u.uuid = ?'});
    //mongoService.deleteOneWithFilter(collection, {'id': 2});

    /*
    const pipeline = [
        {
            '$match': {
                'operationType': 'insert'
            },
        }
    ];
    async function monitorListingsUsingEventEmitter(client: MongoClient, pipeline) {
        const collection = client.db(MONGO_DB_NAME).collection('MONGO_COLLECTION_NAME');
        const changeStream = collection.watch(pipeline);
        changeStream.on('change', (next) => {
            console.log(next);
        });
    }
    await monitorListingsUsingEventEmitter(mongoHelper.client, pipeline);
    */

    mongoHelper.disconnect();
});
socketService.runSocket(app, httpServer, PORT, io);


