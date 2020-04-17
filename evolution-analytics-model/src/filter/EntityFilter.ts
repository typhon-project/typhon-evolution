export class EntityFilter {

    private _name: string;
    private _latestVersion: number;
    private _versions?: number[];

    constructor() {
    }

    get name(): string {
        return this._name;
    }

    get latestVersion(): number {
        return this._latestVersion;
    }

    get versions(): number[] {
        return this._versions;
    }

    set name(name: string) {
        this._name = name;
    }

    set latestVersion(latestVersion: number) {
        this._latestVersion = latestVersion;
    }

    set versions(versions: number[]) {
        this._versions = versions;
    }

    toString(): string {
        return `{ _name: ${this._name}, _latestVersion: ${this._latestVersion}, _versions: ${this._versions} }`;
    }
}
