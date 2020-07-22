import { MongoCollection } from "./MongoCollection";
export declare class NormalizedQuery extends MongoCollection {
    private _normalizedForm;
    private _displayableForm;
    private _count;
    constructor(normalizedForm: string, displayableForm: string, count: number, id?: string);
    get collectionName(): string;
    get normalizedForm(): string;
    get displayableForm(): string;
    get count(): number;
    set normalizedForm(_normalizedForm: string);
    set displayableForm(_displayableForm: string);
    set count(_count: number);
    toString(): string;
}
