export class EntityHistoryFilter {

    private _name: string;
    private _updateDate: Date;
    private _modelVersion: number;
    private _dataSize: number;
    private _nbOfQueries:number;
    private _nbOfSelect:number;
    private _nbOfInsert:number;
    private _nbOfUpdate:number;
    private _nbOfDelete:number;

    constructor() {
    }

    get name(): string {
        return this._name;
    }

    get updateDate(): Date {
        return this._updateDate;
    }

    get modelVersion(): number {
        return this._modelVersion;
    }

    get dataSize(): number {
        return this._dataSize;
    }

    get nbOfQueries(): number {
        return this._nbOfQueries;
    }

    get nbOfSelect(): number {
        return this._nbOfSelect;
    }

    get nbOfInsert(): number {
        return this._nbOfInsert;
    }

    get nbOfUpdate(): number {
        return this._nbOfUpdate;
    }

    get nbOfDelete(): number {
        return this._nbOfDelete;
    }

    set name(name: string) {
        this._name = name;
    }

    set updateDate(updateDate: Date) {
        this._updateDate = updateDate;
    }

    set modelVersion(modelVersion: number) {
        this._modelVersion = modelVersion;
    }

    set dataSize(dataSize: number) {
        this._dataSize = dataSize;
    }

    set nbOfQueries(nbOfQueries: number) {
        this._nbOfQueries = nbOfQueries;
    }

    set nbOfSelect(nbOfSelect: number) {
        this._nbOfSelect = nbOfSelect;
    }

    set nbOfInsert(nbOfInsert: number) {
        this._nbOfInsert = nbOfInsert;
    }

    set nbOfUpdate(nbOfUpdate: number) {
        this._nbOfUpdate = nbOfUpdate;
    }

    set nbOfDelete(nbOfDelete: number) {
        this._nbOfDelete = nbOfDelete;
    }

    toString(): string {
        return `{ _name: ${this._name}, _updateDate: ${this._name}, _modelVersion: ${this._name}, _dataSize: ${this._name}, _nbOfQueries: ${this._name}, _nbOfSelect: ${this._name}, _nbOfInsert: ${this._name}, _nbOfUpdate: ${this._name}, _nbOfDelete: ${this._name} }`;
    }
}
