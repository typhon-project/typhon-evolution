import {MongoCollection} from "./MongoCollection";

export class Entity extends MongoCollection {

    private _name: string;
    private _latestVersion: number;
    private _versions?: number[];

    constructor(id: string, name: string, latestVersion: number, versions?: number[]) {
        super();
        this._id = id;
        this._name = name;
        this._latestVersion = latestVersion;
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

    get versions(): number[] {
        return this._versions;
    }

    set name(name: string) {
        this._name = name;
    }

    set latestVersion(latestVersion: number) {
        this._latestVersion = latestVersion;
    }

    set versions(versions: number[]) {
        this._versions = versions;
    }

    toString(): string {
        return `{ _id: ${this._id}, _name: ${this._name}, _latestVersion: ${this._latestVersion}, _versions: ${this._versions} }`;
    }
}
