"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MongoCollection_1 = require("./MongoCollection");
class NormalizedQuery extends MongoCollection_1.MongoCollection {
    constructor(normalizedForm, displayableForm, count, id) {
        super(id);
        this._normalizedForm = normalizedForm;
        this._displayableForm = displayableForm;
        this._count = count;
    }
    get collectionName() {
        return MongoCollection_1.MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME;
    }
    get normalizedForm() {
        return this._normalizedForm;
    }
    get displayableForm() {
        return this._displayableForm;
    }
    get count() {
        return this._count;
    }
    set normalizedForm(_normalizedForm) {
        this._normalizedForm = _normalizedForm;
    }
    set displayableForm(_displayableForm) {
        this._displayableForm = _displayableForm;
    }
    set count(_count) {
        this._count = _count;
    }
    toString() {
        return `{ id: ${this.id}, normalizedForm: ${this.normalizedForm}, displayableForm: ${this.displayableForm}, count: ${this.count} }`;
    }
}
exports.NormalizedQuery = NormalizedQuery;
//# sourceMappingURL=NormalizedQuery.js.map