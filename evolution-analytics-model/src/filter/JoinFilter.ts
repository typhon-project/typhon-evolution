export class JoinFilter {

    public entity1: string;
    public entity2: string;
    public relation1?: string;
    public relation2?: string;

    constructor() {
    }

    toString(): string {
        return `{ _entity1: ${this.entity1}, entity2: ${this.entity2}, relation1: ${this.relation1}, relation2: ${this.relation2} }`;
    }

}
