export class AnalyticsModel {
    private id: number;
    private query?: string;

    constructor(id: number, query?: string) {
        this.id = id;
        this.query = query;
    }

    get getId(): number {
        return this.id;
    }

    get getQuery(): string {
        return this.query;
    }

    set setId(id: number) {
        this.id = id;
    }

    set setQuery(query: string) {
        this.query = query;
    }

    toString(): string {
        return `{ id: ${this.id}, query: ${this.query} }`;
    }
}
