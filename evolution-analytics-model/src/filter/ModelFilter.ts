export class ModelFilter {

    private _version: number;
    private _date: Date;

    constructor() {
    }

    get version(): number {
        return this._version;
    }

    get date(): Date {
        return this._date;
    }

    set version(version: number) {
        this._version = version;
    }

    set date(date: Date) {
        this._date = date;
    }

    toString(): string {
        return `{ _version: ${this._version}, _date: ${this._date} }`;
    }
}
