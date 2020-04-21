export class EntityFilter {

    public name: string;
    public latestVersion: number;
    public versions?: number[];

    constructor() {
    }

    toString(): string {
        return `{ name: ${this.name}, latestVersion: ${this.latestVersion}, versions: ${this.versions} }`;
    }
}
