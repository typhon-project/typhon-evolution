export abstract class MongoCollection {

    public static NORMALIZED_QUERY_COLLECTION_NAME: string = 'QLNormalizedQuery';
    public static QUERY_COLLECTION_NAME: string = 'QLQuery';
    public static ENTITY_COLLECTION_NAME: string = 'TyphonEntity';
    public static ENTITY_HISTORY_COLLECTION_NAME: string = 'TyphonEntityHistory';
    public static MODEL_COLLECTION_NAME: string = 'TyphonModel';

    _id: string;

    get id(): string {
        return this._id;
    }


    set id(id: string) {
        this._id = id;
    }

    abstract get collectionName(): string;
}
