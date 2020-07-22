export declare abstract class MongoCollection {
    static NORMALIZED_QUERY_COLLECTION_NAME: string;
    static QUERY_COLLECTION_NAME: string;
    static ENTITY_COLLECTION_NAME: string;
    static ENTITY_HISTORY_COLLECTION_NAME: string;
    static MODEL_COLLECTION_NAME: string;
    _id: string;
    constructor(id?: string);
    get id(): string;
    set id(id: string);
    abstract get collectionName(): string;
}
