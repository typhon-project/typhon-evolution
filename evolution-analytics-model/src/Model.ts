import { ObjectId } from "mongodb";

export class Model {

    private _id: ObjectId;
    private _version: number;
    private _date: Date;

    constructor(id: ObjectId, version: number, date: Date) {
        this._id = id;
        this._version = version;
        this._date = date;
    }

    get id(): ObjectId {
        return this._id;
    }

    get version(): number {
        return this._version;
    }

    get date(): Date {
        return this._date;
    }

    set id(id: ObjectId) {
        this._id = id;
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
