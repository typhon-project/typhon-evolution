export declare class Join {
    private _entity1;
    private _entity2;
    private _relation1?;
    private _relation2?;
    constructor(entity1: string, entity2: string, relation1?: string, relation2?: string);
    get entity1(): string;
    get entity2(): string;
    get relation1(): string;
    get relation2(): string;
    set entity1(entity1: string);
    set entity2(entity2: string);
    set relation1(relation1: string);
    set relation2(relation2: string);
    toString(): string;
}
