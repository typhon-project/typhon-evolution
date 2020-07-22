export declare class Selector {
    private _entity;
    private _attribute;
    private _operator;
    constructor(entity: string, attribute: string, operator: string);
    get entity(): string;
    get attribute(): string;
    get operator(): string;
    set entity(entity: string);
    set attribute(attribute: string);
    set operator(operator: string);
    toString(): string;
}
