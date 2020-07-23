import { MongoCollection } from "./MongoCollection";
export declare class Entity extends MongoCollection {
    private _name;
    private _latestVersion;
    private _dbName;
    private _dbType;
    private _versions?;
    constructor(name: string, latestVersion: number, dbName: string, dbType: string, versions?: number[], id?: string);
    get collectionName(): string;
    get name(): string;
    get latestVersion(): number;
    get dbName(): string;
    get dbType(): string;
    get versions(): number[];
    set name(name: string);
    set latestVersion(latestVersion: number);
    set dbName(value: string);
    set dbType(value: string);
    set versions(versions: number[]);
    toString(): string;
}
