export class Join {

    private _entity1: string;
    private _entity2: string;
    private _relation1?: string;
    private _relation2?: string;

    constructor(entity1: string, entity2: string, relation1?: string, relation2?: string) {
        this._entity1 = entity1;
        this._entity2 = entity2;
        this._relation1 = relation1;
        this._relation2 = relation2;
    }

    get entity1(): string {
        return this._entity1;
    }

    get entity2(): string {
        return this._entity2;
    }

    get relation1(): string {
        return this._relation1;
    }

    get relation2(): string {
        return this._relation2;
    }

    set entity1(entity1: string) {
        this._entity1 = entity1;
    }

    set entity2(entity2: string) {
        this._entity2 = entity2;
    }

    set relation1(relation1: string) {
        this._relation1 = relation1;
    }

    set relation2(relation2: string) {
        this._relation2 = relation2;
    }

    toString(): string {
        return `{ entity1: ${this.entity1}, entity2: ${this.entity2}, relation1: ${this.relation1}, relation2: ${this.relation2} }`;
    }

}
