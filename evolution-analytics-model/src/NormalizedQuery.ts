import { ObjectId } from "mongodb";

export class NormalizedQuery {

    private _id: ObjectId;
    private _normalizedForm: string;
    private _displayableForm: string;
    private _count: number;

    constructor(id: ObjectId, normalizedForm: string, displayableForm: string, count: number) {
        this._id = id;
        this._normalizedForm = normalizedForm;
        this._displayableForm = displayableForm;
        this._count = count;
    }

    get id(): ObjectId {
        return this._id;
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

    set id(_id: ObjectId) {
        this._id = _id;
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
        return `{ _id: ${this._id}, _normalizedForm: ${this._normalizedForm}, _displayableForm: ${this._displayableForm}, _count: ${this._count} }`;
    }
}
