import {MongoClient} from 'mongodb';

export class MongoHelper {

    public client: MongoClient;

    constructor() {
    }

    public async connectWithAuthentification(url, username, password): Promise<any> {
        try {
            if (!this.client) {
                console.log('Connecting to Mongo database with authentification');
                this.client = await MongoClient.connect(url, {useNewUrlParser: true, useUnifiedTopology: true, auth: {user: username, password: password}});
                console.log('Connection to Mongo database successful with authentification');
            }
        } catch (error) {
            console.log('Error while connecting to Mongo database');
        }
    }

    public async connect(url): Promise<any> {
        try {
            if (!this.client) {
                console.log('Connecting to Mongo database');
                this.client = await MongoClient.connect(url, {useNewUrlParser: true, useUnifiedTopology: true});
                console.log('Connection to Mongo database successful');
            }
        } catch (error) {
            console.log('Error while connecting to Mongo database');
        }
    }

    public disconnect() {
        if (!this.client) {
            this.client.close();
        }
    }

}
