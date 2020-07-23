"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MongoCollection_1 = require("./MongoCollection");
class Query extends MongoCollection_1.MongoCollection {
    constructor(normalizedQueryId, query, type, executionDate, executionTime, modelVersion, mainEntities, selectors, joins, implicitInsertedEntities, id) {
        super(id);
        this._normalizedQueryId = normalizedQueryId;
        this._query = query;
        this._type = type;
        this._executionDate = executionDate;
        this._executionTime = executionTime;
        this._modelVersion = modelVersion;
        this._mainEntities = mainEntities;
        this._selectors = selectors;
        this._joins = joins;
        this._implicitInsertedEntities = implicitInsertedEntities;
    }
    get collectionName() {
        return MongoCollection_1.MongoCollection.QUERY_COLLECTION_NAME;
    }
    get normalizedQueryId() {
        return this._normalizedQueryId;
    }
    get query() {
        return this._query;
    }
    get type() {
        return this._type;
    }
    get executionDate() {
        return this._executionDate;
    }
    get executionTime() {
        return this._executionTime;
    }
    get modelVersion() {
        return this._modelVersion;
    }
    get mainEntities() {
        return this._mainEntities;
    }
    get selectors() {
        return this._selectors;
    }
    get joins() {
        return this._joins;
    }
    get implicitInsertedEntities() {
        return this._implicitInsertedEntities;
    }
    set normalizedQueryId(normalizedQueryId) {
        this._normalizedQueryId = normalizedQueryId;
    }
    set query(query) {
        this._query = query;
    }
    set type(type) {
        this._type = type;
    }
    set executionDate(executionDate) {
        this._executionDate = executionDate;
    }
    set executionTime(executionTime) {
        this._executionTime = executionTime;
    }
    set modelVersion(modelVersion) {
        this._modelVersion = modelVersion;
    }
    set mainEntities(mainEntities) {
        this._mainEntities = mainEntities;
    }
    set selectors(selectors) {
        this._selectors = selectors;
    }
    set joins(joins) {
        this._joins = joins;
    }
    set implicitInsertedEntities(implicitInsertedEntities) {
        this._implicitInsertedEntities = implicitInsertedEntities;
    }
    toString() {
        return `{ id: ${this.id}, normalizedQueryId: ${this.normalizedQueryId}, query: ${this.query}, type: ${this.type}, executionDate: ${this.executionDate}, executionTime: ${this.executionTime}, modelVersion: ${this.modelVersion}, mainEntities: ${this.mainEntities}, selectors: ${this.selectors}, joins: ${this.joins}, implicitInsertedEntities: ${this.implicitInsertedEntities}  }`;
    }
}
exports.Query = Query;
//# sourceMappingURL=Query.js.map