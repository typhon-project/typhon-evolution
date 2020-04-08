export class Query {

    private id: number;

    constructor(id: number) {
        this.id = id;
    }

    get getId(): number {
        return this.id;
    }

    set setId(id: number) {
        this.id = id;
    }

    toString(): string {
        return `{ id: ${this.id} }`;
    }
}
