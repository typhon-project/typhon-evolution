export class SelectorFilter {

    public entity: string;
    public attribute: string;
    public operator: string;

    constructor() {
    }

    toString(): string {
        return `{ entity: ${this.entity}, attribute: ${this.attribute}, operator: ${this.operator} }`;
    }

}
