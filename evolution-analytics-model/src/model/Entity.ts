import {MongoCollection} from "./MongoCollection";

export class Entity extends MongoCollection {

    private _name: string;
    private _latestVersion: number;
    private _dbName: string;
    private _dbType: string;
    private _versions?: number[];

    constructor(name: string, latestVersion: number, dbName: string, dbType: string, versions?: number[], id?: string) {
        super(id);
        this._name = name;
        this._latestVersion = latestVersion;
        this._dbName = dbName;
        this._dbType = dbType;
        this._versions = versions;
    }

    get collectionName(): string {
        return MongoCollection.ENTITY_COLLECTION_NAME;
    }

    get name(): string {
        return this._name;
    }

    get latestVersion(): number {
        return this._latestVersion;
    }

    get dbName(): string {
        return this._dbName;
    }

    get dbType(): string {
        return this._dbType;
    }

    get versions(): number[] {
        return this._versions;
    }

    set name(name: string) {
        this._name = name;
    }

    set latestVersion(latestVersion: number) {
        this._latestVersion = latestVersion;
    }

    set dbName(value: string) {
        this._dbName = value;
    }

    set dbType(value: string) {
        this._dbType = value;
    }

    set versions(versions: number[]) {
        this._versions = versions;
    }

    toString(): string {
        return `{ id: ${this.id}, name: ${this.name}, latestVersion: ${this.latestVersion}, dbName: ${this.dbName}, dbType: ${this.dbType}, versions: ${this.versions} }`;
    }
}
