"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class NormalizedQueryFilter {
    constructor() {
    }
    toString() {
        return `{ normalizedForm: ${this.normalizedForm}, displayableForm: ${this.displayableForm}, count: ${this.count} }`;
    }
}
exports.NormalizedQueryFilter = NormalizedQueryFilter;
//# sourceMappingURL=NormalizedQueryFilter.js.map