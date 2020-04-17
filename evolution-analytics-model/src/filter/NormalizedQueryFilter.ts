export class NormalizedQueryFilter {

    private _normalizedForm: string;
    private _displayableForm: string;
    private _count: number;

    constructor() {
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
        return `{ _normalizedForm: ${this._normalizedForm}, _displayableForm: ${this._displayableForm}, _count: ${this._count} }`;
    }
}
