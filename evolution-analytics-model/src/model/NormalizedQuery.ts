import {MongoCollection} from "./MongoCollection";

export class NormalizedQuery extends MongoCollection {

    private _normalizedForm: string;
    private _displayableForm: string;
    private _count: number;

    constructor(normalizedForm: string, displayableForm: string, count: number, id?: string) {
        super(id);
        this._normalizedForm = normalizedForm;
        this._displayableForm = displayableForm;
        this._count = count;
    }

    get collectionName(): string {
        return MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME;
    }

    get normalizedForm(): string {
        return this._normalizedForm;
    }

    get displayableForm(): string {
        return this._displayableForm;
    }

    get count(): number {
        return this._count;
    }

    set normalizedForm(_normalizedForm: string) {
        this._normalizedForm = _normalizedForm;
    }

    set displayableForm(_displayableForm: string) {
        this._displayableForm = _displayableForm;
    }

    set count(_count: number) {
        this._count = _count;
    }

    toString(): string {
        return `{ id: ${this.id}, normalizedForm: ${this.normalizedForm}, displayableForm: ${this.displayableForm}, count: ${this.count} }`;
    }
}
