export class NormalizedQuery {

    private id: number;
    private normalizedForm: string;
    private displayableForm: string;

    constructor(id: number, normalizedForm: string, displayableForm: string) {
        this.id = id;
        this.normalizedForm = normalizedForm;
        this.displayableForm = displayableForm;
    }

    get getId(): number {
        return this.id;
    }

    get getNormalizedForm(): string {
        return this.normalizedForm;
    }

    get getDisplayableForm(): string {
        return this.displayableForm;
    }

    set setId(id: number) {
        this.id = id;
    }

    set setNormalizedForm(normalizedForm: string) {
        this.normalizedForm = normalizedForm;
    }

    set setDisplayableForm(displayableForm: string) {
        this.displayableForm = displayableForm;
    }

    toString(): string {
        return `{ id: ${this.id}, normalizedForm: ${this.normalizedForm}, displayableForm: ${this.displayableForm} }`;
    }
}
