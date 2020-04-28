export class EntityFilter {

    public name: string;
    public latestVersion: number;
    public dbName: string;
    public dbType: string;
    public versions?: number[];

    constructor() {
    }

    toString(): string {
        return `{ name: ${this.name}, latestVersion: ${this.latestVersion}, dbName: ${this.dbName}, dbType: ${this.dbType}, versions: ${this.versions} }`;
    }
}
