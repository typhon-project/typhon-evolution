"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MongoCollection_1 = require("./MongoCollection");
class Entity extends MongoCollection_1.MongoCollection {
    constructor(name, latestVersion, dbName, dbType, versions, id) {
        super(id);
        this._name = name;
        this._latestVersion = latestVersion;
        this._dbName = dbName;
        this._dbType = dbType;
        this._versions = versions;
    }
    get collectionName() {
        return MongoCollection_1.MongoCollection.ENTITY_COLLECTION_NAME;
    }
    get name() {
        return this._name;
    }
    get latestVersion() {
        return this._latestVersion;
    }
    get dbName() {
        return this._dbName;
    }
    get dbType() {
        return this._dbType;
    }
    get versions() {
        return this._versions;
    }
    set name(name) {
        this._name = name;
    }
    set latestVersion(latestVersion) {
        this._latestVersion = latestVersion;
    }
    set dbName(value) {
        this._dbName = value;
    }
    set dbType(value) {
        this._dbType = value;
    }
    set versions(versions) {
        this._versions = versions;
    }
    toString() {
        return `{ id: ${this.id}, name: ${this.name}, latestVersion: ${this.latestVersion}, dbName: ${this.dbName}, dbType: ${this.dbType}, versions: ${this.versions} }`;
    }
}
exports.Entity = Entity;
//# sourceMappingURL=Entity.js.map