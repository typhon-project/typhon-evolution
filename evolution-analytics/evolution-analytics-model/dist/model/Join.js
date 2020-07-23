"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Join {
    constructor(entity1, entity2, relation1, relation2) {
        this._entity1 = entity1;
        this._entity2 = entity2;
        this._relation1 = relation1;
        this._relation2 = relation2;
    }
    get entity1() {
        return this._entity1;
    }
    get entity2() {
        return this._entity2;
    }
    get relation1() {
        return this._relation1;
    }
    get relation2() {
        return this._relation2;
    }
    set entity1(entity1) {
        this._entity1 = entity1;
    }
    set entity2(entity2) {
        this._entity2 = entity2;
    }
    set relation1(relation1) {
        this._relation1 = relation1;
    }
    set relation2(relation2) {
        this._relation2 = relation2;
    }
    toString() {
        return `{ entity1: ${this.entity1}, entity2: ${this.entity2}, relation1: ${this.relation1}, relation2: ${this.relation2} }`;
    }
}
exports.Join = Join;
//# sourceMappingURL=Join.js.map