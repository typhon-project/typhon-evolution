import {SelectorFilter} from "./SelectorFilter";
import {JoinFilter} from "./JoinFilter";

export class QueryFilter {

    public normalizedQueryId: string;
    public query: string;
    public type: string;
    public executionDate: Date;
    public executionTime: Date;
    public modelVersion: number;
    public mainEntities?: string[];
    public selectors?: SelectorFilter[];
    public joins?: JoinFilter[];
    public implicitInsertedEntities?: string[];

    constructor() {
    }

    toString(): string {
        return `{ normalizedQueryId: ${this.normalizedQueryId}, query: ${this.query}, type: ${this.type}, executionDate: ${this.executionDate}, executionTime: ${this.executionTime}, modelVersion: ${this.modelVersion}, mainEntities: ${this.mainEntities}, selectors: ${this.selectors}, joins: ${this.joins}, implicitInsertedEntities: ${this.implicitInsertedEntities}  }`;
    }
}
