export class Selector {

    private _entity: string;
    private _attribute: string;
    private _operator: string;

    constructor(entity: string, attribute: string, operator: string) {
        this._entity = entity;
        this._attribute = attribute;
        this._operator = operator;
    }

    get entity(): string {
        return this._entity;
    }

    get attribute(): string {
        return this._attribute;
    }

    get operator(): string {
        return this._operator;
    }

    set entity(entity: string) {
        this._entity = entity;
    }

    set attribute(attribute: string) {
        this._attribute = attribute;
    }

    set operator(operator: string) {
        this._operator = operator;
    }

    toString(): string {
        return `{ _entity: ${this._entity}, _attribute: ${this._attribute}, _operator: ${this._operator} }`;
    }

}
