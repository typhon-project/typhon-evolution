import { ObjectId } from "mongodb";

export class Entity {

    private _id: ObjectId;
    private _name: string;
    private _latestVersion: number;
    private _versions?: number[];

    constructor(id: ObjectId, name: string, latestVersion: number, versions?: number[]) {
        this._id = id;
        this._name = name;
        this._latestVersion = latestVersion;
        this._versions = versions;
    }

    get id(): ObjectId {
        return this._id;
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

    set id(id: ObjectId) {
        this._id = id;
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
