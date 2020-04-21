export class EntityHistoryFilter {

    public name: string;
    public updateDate: Date;
    public modelVersion: number;
    public dataSize: number;
    public nbOfQueries:number;
    public nbOfSelect:number;
    public nbOfInsert:number;
    public nbOfUpdate:number;
    public nbOfDelete:number;

    constructor() {
    }

    toString(): string {
        return `{ name: ${this.name}, updateDate: ${this.name}, modelVersion: ${this.name}, dataSize: ${this.name}, nbOfQueries: ${this.name}, nbOfSelect: ${this.name}, nbOfInsert: ${this.name}, nbOfUpdate: ${this.name}, nbOfDelete: ${this.name} }`;
    }
}
