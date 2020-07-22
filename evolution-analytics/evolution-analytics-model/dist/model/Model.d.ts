import { MongoCollection } from "./MongoCollection";
export declare class Model extends MongoCollection {
    private _version;
    private _date;
    constructor(version: number, date: Date, id?: string);
    get collectionName(): string;
    get version(): number;
    get date(): Date;
    set version(version: number);
    set date(date: Date);
    toString(): string;
}
