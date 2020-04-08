import * as mongo from 'mongodb';

export class MongoHelper {

    public client: mongo.MongoClient;

    constructor() {
    }

    public async connect(url): Promise<any> {
        try {
            if (!this.client) {
                console.log('Connecting to Mongo database');
                this.client = await mongo.MongoClient.connect(url, {useNewUrlParser: true, useUnifiedTopology: true });
                console.log('Connection to Mongo database successful');
            }
        } catch (error) {
            console.log('Error while connecting to Mongo database');
            console.error(error);
        }
    }

    public disconnect() {
        if (!this.client) {
            this.client.close();
        }
    }

}
