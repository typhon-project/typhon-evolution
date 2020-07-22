"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class MongoCollection {
    constructor(id) {
        if (id) {
            this._id = id;
        }
    }
    get id() {
        return this._id;
    }
    set id(id) {
        this._id = id;
    }
}
exports.MongoCollection = MongoCollection;
MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME = 'QLNormalizedQuery';
MongoCollection.QUERY_COLLECTION_NAME = 'QLQuery';
MongoCollection.ENTITY_COLLECTION_NAME = 'TyphonEntity';
MongoCollection.ENTITY_HISTORY_COLLECTION_NAME = 'TyphonEntityHistory';
MongoCollection.MODEL_COLLECTION_NAME = 'TyphonModel';
//# sourceMappingURL=MongoCollection.js.map