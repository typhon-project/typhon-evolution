import {Selector} from "./Selector";
import {Join} from "./Join";
import {MongoCollection} from "./MongoCollection";

export class Query extends MongoCollection {

    private _normalizedQueryId: string;
    private _query: string;
    private _type: string;
    private _executionDate: Date;
    private _executionTime: Date;
    private _modelVersion: number;
    private _mainEntities?: string[];
    private _selectors?: Selector[];
    private _joins?: Join[];
    private _implicitInsertedEntities?: string[];


    constructor(id: string, normalizedQueryId: string, query: string, type: string, executionDate: Date, executionTime: Date, modelVersion: number, mainEntities?: string[], selectors?: Selector[], joins?: Join[], implicitInsertedEntities?: string[]) {
        super();
        this._id = id;
        this._normalizedQueryId = normalizedQueryId;
        this._query = query;
        this._type = type;
        this._executionDate = executionDate;
        this._executionTime = executionTime;
        this._modelVersion = modelVersion;
        this._mainEntities = mainEntities;
        this._selectors = selectors;
        this._joins = joins;
        this._implicitInsertedEntities = implicitInsertedEntities;
    }

    get collectionName(): string {
        return MongoCollection.QUERY_COLLECTION_NAME;
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

    get selectors(): Selector[] {
        return this._selectors;
    }

    get joins(): Join[] {
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

    set selectors(selectors: Selector[]) {
        this._selectors = selectors;
    }

    set joins(joins: Join[]) {
        this._joins = joins;
    }

    set implicitInsertedEntities(implicitInsertedEntities: string[]) {
        this._implicitInsertedEntities = implicitInsertedEntities;
    }

    toString(): string {
        return `{ _id: ${this._id}, _normalizedQueryId: ${this._normalizedQueryId}, _query: ${this._query}, _type: ${this._type}, _executionDate: ${this._executionDate}, _executionTime: ${this._executionTime}, _modelVersion: ${this._modelVersion}, _mainEntities: ${this._mainEntities}, _selectors: ${this._selectors}, _joins: ${this._joins}, _implicitInsertedEntities: ${this._implicitInsertedEntities}  }`;
    }
}
