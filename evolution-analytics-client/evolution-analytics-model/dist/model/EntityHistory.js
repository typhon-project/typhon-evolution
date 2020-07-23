"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MongoCollection_1 = require("./MongoCollection");
class EntityHistory extends MongoCollection_1.MongoCollection {
    constructor(name, updateDate, modelVersion, dataSize, nbOfQueries, nbOfSelect, nbOfInsert, nbOfUpdate, nbOfDelete, id) {
        super(id);
        this._name = name;
        this._updateDate = updateDate;
        this._modelVersion = modelVersion;
        this._dataSize = dataSize;
        this._nbOfQueries = nbOfQueries;
        this._nbOfSelect = nbOfSelect;
        this._nbOfInsert = nbOfInsert;
        this._nbOfUpdate = nbOfUpdate;
        this._nbOfDelete = nbOfDelete;
    }
    get collectionName() {
        return MongoCollection_1.MongoCollection.ENTITY_HISTORY_COLLECTION_NAME;
    }
    get name() {
        return this._name;
    }
    get updateDate() {
        return this._updateDate;
    }
    get modelVersion() {
        return this._modelVersion;
    }
    get dataSize() {
        return this._dataSize;
    }
    get nbOfQueries() {
        return this._nbOfQueries;
    }
    get nbOfSelect() {
        return this._nbOfSelect;
    }
    get nbOfInsert() {
        return this._nbOfInsert;
    }
    get nbOfUpdate() {
        return this._nbOfUpdate;
    }
    get nbOfDelete() {
        return this._nbOfDelete;
    }
    set name(name) {
        this._name = name;
    }
    set updateDate(updateDate) {
        this._updateDate = updateDate;
    }
    set modelVersion(modelVersion) {
        this._modelVersion = modelVersion;
    }
    set dataSize(dataSize) {
        this._dataSize = dataSize;
    }
    set nbOfQueries(nbOfQueries) {
        this._nbOfQueries = nbOfQueries;
    }
    set nbOfSelect(nbOfSelect) {
        this._nbOfSelect = nbOfSelect;
    }
    set nbOfInsert(nbOfInsert) {
        this._nbOfInsert = nbOfInsert;
    }
    set nbOfUpdate(nbOfUpdate) {
        this._nbOfUpdate = nbOfUpdate;
    }
    set nbOfDelete(nbOfDelete) {
        this._nbOfDelete = nbOfDelete;
    }
    toString() {
        return `{ id: ${this.id}, name: ${this.name}, updateDate: ${this.name}, modelVersion: ${this.name}, dataSize: ${this.name}, nbOfQueries: ${this.name}, nbOfSelect: ${this.name}, nbOfInsert: ${this.name}, nbOfUpdate: ${this.name}, nbOfDelete: ${this.name} }`;
    }
}
exports.EntityHistory = EntityHistory;
//# sourceMappingURL=EntityHistory.js.map