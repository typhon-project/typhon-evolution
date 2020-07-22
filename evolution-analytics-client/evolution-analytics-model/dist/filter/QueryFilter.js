"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class QueryFilter {
    constructor() {
    }
    toString() {
        return `{ normalizedQueryId: ${this.normalizedQueryId}, query: ${this.query}, type: ${this.type}, executionDate: ${this.executionDate}, executionTime: ${this.executionTime}, modelVersion: ${this.modelVersion}, mainEntities: ${this.mainEntities}, selectors: ${this.selectors}, joins: ${this.joins}, implicitInsertedEntities: ${this.implicitInsertedEntities}  }`;
    }
}
exports.QueryFilter = QueryFilter;
//# sourceMappingURL=QueryFilter.js.map