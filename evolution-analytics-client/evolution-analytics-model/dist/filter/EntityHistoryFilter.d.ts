export declare class EntityHistoryFilter {
    name: string;
    updateDate: Date;
    modelVersion: number;
    dataSize: number;
    nbOfQueries: number;
    nbOfSelect: number;
    nbOfInsert: number;
    nbOfUpdate: number;
    nbOfDelete: number;
    constructor();
    toString(): string;
}
