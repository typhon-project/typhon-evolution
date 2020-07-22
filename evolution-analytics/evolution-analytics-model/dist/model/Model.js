"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MongoCollection_1 = require("./MongoCollection");
class Model extends MongoCollection_1.MongoCollection {
    constructor(version, date, id) {
        super(id);
        this._version = version;
        this._date = date;
    }
    get collectionName() {
        return MongoCollection_1.MongoCollection.MODEL_COLLECTION_NAME;
    }
    get version() {
        return this._version;
    }
    get date() {
        return this._date;
    }
    set version(version) {
        this._version = version;
    }
    set date(date) {
        this._date = date;
    }
    toString() {
        return `{ id: ${this.id}, version: ${this.version}, date: ${this.date} }`;
    }
}
exports.Model = Model;
//# sourceMappingURL=Model.js.map