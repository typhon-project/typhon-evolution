"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Selector {
    constructor(entity, attribute, operator) {
        this._entity = entity;
        this._attribute = attribute;
        this._operator = operator;
    }
    get entity() {
        return this._entity;
    }
    get attribute() {
        return this._attribute;
    }
    get operator() {
        return this._operator;
    }
    set entity(entity) {
        this._entity = entity;
    }
    set attribute(attribute) {
        this._attribute = attribute;
    }
    set operator(operator) {
        this._operator = operator;
    }
    toString() {
        return `{ entity: ${this.entity}, attribute: ${this.attribute}, operator: ${this.operator} }`;
    }
}
exports.Selector = Selector;
//# sourceMappingURL=Selector.js.map