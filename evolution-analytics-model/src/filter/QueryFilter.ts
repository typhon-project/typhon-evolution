import {SelectorFilter} from "./SelectorFilter";
import {JoinFilter} from "./JoinFilter";

export class QueryFilter {

    private _normalizedQueryId: string;
    private _query: string;
    private _type: string;
    private _executionDate: Date;
    private _executionTime: Date;
    private _modelVersion: number;
    private _mainEntities?: string[];
    private _selectors?: SelectorFilter[];
    private _joins?: JoinFilter[];
    private _implicitInsertedEntities?: string[];


    constructor() {
    }

    get normalizedQueryId(): string {
        return this._normalizedQueryId;
    }

    get query(): string {
        return this._query;
    }

    get type(): string {
        return this._type;
    }

    get executionDate(): Date {
        return this._executionDate;
    }

    get executionTime(): Date {
        return this._executionTime;
    }

    get modelVersion(): number {
        return this._modelVersion;
    }

    get mainEntities(): string[] {
        return this._mainEntities;
    }

    get selectors(): SelectorFilter[] {
        return this._selectors;
    }

    get joins(): JoinFilter[] {
        return this._joins;
    }

    get implicitInsertedEntities(): string[] {
        return this._implicitInsertedEntities;
    }

    set normalizedQueryId(normalizedQueryId: string) {
        this._normalizedQueryId = normalizedQueryId;
    }

    set query(query: string) {
        this._query = query;
    }

    set type(type: string) {
        this._type = type;
    }

    set executionDate(executionDate: Date) {
        this._executionDate = executionDate;
    }

    set executionTime(executionTime: Date) {
        this._executionTime = executionTime;
    }

    set modelVersion(modelVersion: number) {
        this._modelVersion = modelVersion;
    }

    set mainEntities(mainEntities: string[]) {
        this._mainEntities = mainEntities;
    }

    set selectors(selectors: SelectorFilter[]) {
        this._selectors = selectors;
    }

    set joins(joins: JoinFilter[]) {
        this._joins = joins;
    }

    set implicitInsertedEntities(implicitInsertedEntities: string[]) {
        this._implicitInsertedEntities = implicitInsertedEntities;
    }

    toString(): string {
        return `{ _normalizedQueryId: ${this._normalizedQueryId}, _query: ${this._query}, _type: ${this._type}, _executionDate: ${this._executionDate}, _executionTime: ${this._executionTime}, _modelVersion: ${this._modelVersion}, _mainEntities: ${this._mainEntities}, _selectors: ${this._selectors}, _joins: ${this._joins}, _implicitInsertedEntities: ${this._implicitInsertedEntities}  }`;
    }
}
