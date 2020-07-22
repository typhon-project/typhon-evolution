import { SelectorFilter } from "./SelectorFilter";
import { JoinFilter } from "./JoinFilter";
export declare class QueryFilter {
    normalizedQueryId: string;
    query: string;
    type: string;
    executionDate: Date;
    executionTime: Date;
    modelVersion: number;
    mainEntities?: string[];
    selectors?: SelectorFilter[];
    joins?: JoinFilter[];
    implicitInsertedEntities?: string[];
    constructor();
    toString(): string;
}
