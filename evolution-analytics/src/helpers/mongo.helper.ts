import {MongoClient} from 'mongodb';

export class MongoHelper {

    public client: MongoClient;

    constructor() {
    }

    public async connect(url: string, username?: string, password?: string): Promise<any> {
        try {
            if (!this.client || !this.client.isConnected()) {
                if (username && password) {
                    console.log('Connecting to Mongo database with authentification');
                    this.client = await MongoClient.connect(url, {
                        useNewUrlParser: true,
                        useUnifiedTopology: true,
                        auth: {user: username, password: password}
                    });
                } else {
                    console.log('Connecting to Mongo database without authentification');
                    this.client = await MongoClient.connect(url, {
                            useNewUrlParser: true,
                            useUnifiedTopology: true
                        }
                    );
                }
                console.log('Connection to Mongo database successful');
            }
        } catch (error) {
            console.log('Error while connecting to Mongo database');
        }
    }

    public disconnect() {
        if (this.client) {
            if (this.client.isConnected) {
                this.client.close().then(() => {
                    console.log('Connection to Mongo database closed');
                });
            }
        }
    }

}
