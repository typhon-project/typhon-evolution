import {MongoCollection} from "./MongoCollection";

export class Model extends MongoCollection {

    private _version: number;
    private _date: Date;

    constructor(version: number, date: Date, id?: string) {
        super(id);
        this._version = version;
        this._date = date;
    }

    get collectionName(): string {
        return MongoCollection.MODEL_COLLECTION_NAME;
    }

    get version(): number {
        return this._version;
    }

    get date(): Date {
        return this._date;
    }

    set version(version: number) {
        this._version = version;
    }

    set date(date: Date) {
        this._date = date;
    }

    toString(): string {
        return `{ _id: ${this._id}, _version: ${this._version}, _date: ${this._date} }`;
    }
}
