import express from 'express';
import createServer from 'http';
import SocketIOStatic from 'socket.io';
import * as mongodb from 'mongodb';
import {MongoHelper} from './helpers/mongo.helper';
import {MongoService} from './services/mongo.service';
import {SocketService} from "./services/socket.service";
import {mongoApiRouter} from "./api/routers/mongo.api.router";

const app = express();
app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});
//API routers
app.use('/', mongoApiRouter);

const port = 3000;
const httpServer = createServer.createServer(app);
const io = SocketIOStatic(httpServer);


const mongoService = new MongoService();
const mongoHelper = new MongoHelper();
const socketService = new SocketService();

const MONGO_DB_URL = 'mongodb://localhost:27017/test';
const MONGO_DB_NAME = 'test';
const MONGO_COLLECTION_NAME = 'ANALYTICS';

mongoHelper.connect(MONGO_DB_URL).then(async () => {
    const db: mongodb.Db = mongoHelper.client.db(MONGO_DB_NAME);
    const collection: mongodb.Collection = mongoService.getCollection(db, MONGO_COLLECTION_NAME);
    //mongoService.insertOne(collection, {id: 1, query: "from User u, Order o select u.orders where u.id = ?"});
    //mongoService.insertManyDocuments(collection, [{id: 2, query: "from User u, Order o select o where o.quantity = ?"}]);
    //mongoService.findAll(collection);
    //mongoService.findDocumentsWithFilter(collection, {'id': 1});
    //mongoService.updateOneWithFilter(collection, {'id': 1}, {query: "from User u, Order o select u.orders where u.uuid = ?"});
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
        const collection = client.db(MONGO_DB_NAME).collection("MONGO_COLLECTION_NAME");
        const changeStream = collection.watch(pipeline);
        changeStream.on('change', (next) => {
            console.log(next);
        });
    }
    await monitorListingsUsingEventEmitter(mongoHelper.client, pipeline);
    */

    mongoHelper.disconnect();
});
socketService.runSocket(app, httpServer, port, io);


