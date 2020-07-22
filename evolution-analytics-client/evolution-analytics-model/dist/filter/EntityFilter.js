"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class EntityFilter {
    constructor() {
    }
    toString() {
        return `{ name: ${this.name}, latestVersion: ${this.latestVersion}, dbName: ${this.dbName}, dbType: ${this.dbType}, versions: ${this.versions} }`;
    }
}
exports.EntityFilter = EntityFilter;
//# sourceMappingURL=EntityFilter.js.map