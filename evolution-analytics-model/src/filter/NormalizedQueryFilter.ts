export class NormalizedQueryFilter {

    public normalizedForm: string;
    public displayableForm: string;
    public count: number;

    constructor() {
    }

    toString(): string {
        return `{ normalizedForm: ${this.normalizedForm}, displayableForm: ${this.displayableForm}, count: ${this.count} }`;
    }
}
