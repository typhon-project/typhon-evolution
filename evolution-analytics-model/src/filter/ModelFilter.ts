export class ModelFilter {

    public version: number;
    public date: Date;

    constructor() {
    }

    toString(): string {
        return `{ version: ${this.version}, date: ${this.date} }`;
    }
}
