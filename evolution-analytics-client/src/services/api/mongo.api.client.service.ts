import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {MongoCollection} from 'evolution-analytics-model/dist/model/MongoCollection';
import {NormalizedQuery} from 'evolution-analytics-model/dist/model/NormalizedQuery';
import {NormalizedQueryFilter} from 'evolution-analytics-model/dist/filter/NormalizedQueryFilter';
import {Query} from 'evolution-analytics-model/dist/model/Query';
import {QueryFilter} from 'evolution-analytics-model/dist/filter/QueryFilter';
import {Entity} from 'evolution-analytics-model/dist/model/Entity';
import {EntityFilter} from 'evolution-analytics-model/dist/filter/EntityFilter';
import {EntityHistory} from 'evolution-analytics-model/dist/model/EntityHistory';
import {EntityHistoryFilter} from 'evolution-analytics-model/dist/filter/EntityHistoryFilter';
import {Model} from 'evolution-analytics-model/dist/model/Model';
import {ModelFilter} from 'evolution-analytics-model/dist/filter/ModelFilter';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MongoApiClientService {

  /*Web services endpoint*/
  END_POINT = environment.BACKEND_ENDPOINT;

  /*Resources*/
  FIND = '/find/';
  INSERT = '/insert/'; /*object in the body*/
  INSERT_MANY = '/insert/many/'; /*object array in the body*/
  UPDATE = '/update/'; /*json containing the 'filter' and the 'document' in the body*/
  DELETE = '/delete/'; /*object filter in the body*/

  constructor(private http: HttpClient) {
  }

  /**
   * Find one methods
   */
  public findOneNormalizedQuery(id: string): Observable<NormalizedQuery> {
    return this.http.get<NormalizedQuery>(this.END_POINT + this.FIND + MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME + '/' + id);
  }
  public findOneQuery(id: string): Observable<Query> {
    return this.http.get<Query>(this.END_POINT + this.FIND + MongoCollection.QUERY_COLLECTION_NAME + '/' + id);
  }
  public findOneEntity(id: string): Observable<Entity> {
    return this.http.get<Entity>(this.END_POINT + this.FIND + MongoCollection.ENTITY_COLLECTION_NAME + '/' + id);
  }
  public findOneEntityHistory(id: string): Observable<EntityHistory> {
    return this.http.get<EntityHistory>(this.END_POINT + this.FIND + MongoCollection.ENTITY_HISTORY_COLLECTION_NAME + '/' + id);
  }
  public findOneModel(id: string): Observable<Model> {
    return this.http.get<Model>(this.END_POINT + this.FIND + MongoCollection.MODEL_COLLECTION_NAME + '/' + id);
  }

  /**
   * Find with filter methods
   */
  public findWithFilterNormalizedQueries(filter: NormalizedQueryFilter): Observable<NormalizedQuery[]> {
    return this.http.post<NormalizedQuery[]>(this.END_POINT + this.FIND + MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME, filter);
  }
  public findWithFilterQueries(filter: QueryFilter): Observable<Query[]> {
    return this.http.post<Query[]>(this.END_POINT + this.FIND + MongoCollection.QUERY_COLLECTION_NAME, filter);
  }
  public findWithFilterEntities(filter: EntityFilter): Observable<Entity[]> {
    return this.http.post<Entity[]>(this.END_POINT + this.FIND + MongoCollection.ENTITY_COLLECTION_NAME, filter);
  }
  public findWithFilterEntitiesHistories(filter: EntityHistoryFilter): Observable<EntityHistory[]> {
    return this.http.post<EntityHistory[]>(this.END_POINT + this.FIND + MongoCollection.ENTITY_HISTORY_COLLECTION_NAME, filter);
  }
  public findWithFilterModels(filter: ModelFilter): Observable<Model[]> {
    return this.http.post<Model[]>(this.END_POINT + this.FIND + MongoCollection.MODEL_COLLECTION_NAME, filter);
  }

  /**
   * Find all methods
   */
  public findAllNormalizedQueries(): Observable<NormalizedQuery[]> {
    return this.http.get<NormalizedQuery[]>(this.END_POINT + this.FIND + MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME);
  }
  public findAllQueries(): Observable<Query[]> {
    return this.http.get<Query[]>(this.END_POINT + this.FIND + MongoCollection.QUERY_COLLECTION_NAME);
  }
  public findAllEntities(): Observable<Entity[]> {
    return this.http.get<Entity[]>(this.END_POINT + this.FIND + MongoCollection.ENTITY_COLLECTION_NAME);
  }
  public findAllEntitiesHistories(): Observable<EntityHistory[]> {
    return this.http.get<EntityHistory[]>(this.END_POINT + this.FIND + MongoCollection.ENTITY_HISTORY_COLLECTION_NAME);
  }
  public findAllModels(): Observable<Model[]> {
    return this.http.get<Model[]>(this.END_POINT + this.FIND + MongoCollection.MODEL_COLLECTION_NAME);
  }

  /**
   * Insert one methods (the observable contains the result status number 0-1)
   */
  public insertOneNormalizedQuery(body: NormalizedQuery): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.INSERT + MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME, body);
  }
  public insertOneQuery(body: Query): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.INSERT + MongoCollection.QUERY_COLLECTION_NAME, body);
  }
  public insertOneEntity(body: Entity): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.INSERT + MongoCollection.ENTITY_COLLECTION_NAME, body);
  }
  public insertOneEntityHistory(body: EntityHistory): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.INSERT + MongoCollection.ENTITY_HISTORY_COLLECTION_NAME, body);
  }
  public insertOneModel(body: Model): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.INSERT + MongoCollection.MODEL_COLLECTION_NAME, body);
  }

  /**
   * Insert many methods (the observable contains the result status number 0-1)
   */
  public insertManyNormalizedQueries(body: NormalizedQuery[]): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.INSERT_MANY + MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME, body);
  }
  public insertManyQueries(body: Query[]): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.INSERT_MANY + MongoCollection.QUERY_COLLECTION_NAME, body);
  }
  public insertManyEntities(body: Entity[]): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.INSERT_MANY + MongoCollection.ENTITY_COLLECTION_NAME, body);
  }
  public insertManyEntitiesHistories(body: EntityHistory[]): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.INSERT_MANY + MongoCollection.ENTITY_HISTORY_COLLECTION_NAME, body);
  }
  public insertManyModels(body: Model[]): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.INSERT_MANY + MongoCollection.MODEL_COLLECTION_NAME, body);
  }

  /**
   * Update one methods (the observable contains the result status number 0-1)
   */
  public updateOneNormalizedQuery(filter: NormalizedQueryFilter, document: NormalizedQuery): Observable<number> {
    return this.http.post<number>(
      this.END_POINT + this.UPDATE + MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME,
      `{ filter: ${JSON.stringify(filter)}, document: ${JSON.stringify(document)}`
    );
  }
  public updateOneQuery(filter: QueryFilter, document: Query): Observable<number> {
    return this.http.post<number>(
      this.END_POINT + this.UPDATE + MongoCollection.QUERY_COLLECTION_NAME,
      `{ filter: ${JSON.stringify(filter)}, document: ${JSON.stringify(document)}`
    );
  }
  public updateOneEntity(filter: EntityFilter, document: Entity): Observable<number> {
    return this.http.post<number>(
      this.END_POINT + this.UPDATE + MongoCollection.ENTITY_COLLECTION_NAME,
      `{ filter: ${JSON.stringify(filter)}, document: ${JSON.stringify(document)}`
    );
  }
  public updateOneEntityHistory(filter: EntityHistoryFilter, document: EntityHistory): Observable<number> {
    return this.http.post<number>(
      this.END_POINT + this.UPDATE + MongoCollection.ENTITY_HISTORY_COLLECTION_NAME,
      `{ filter: ${JSON.stringify(filter)}, document: ${JSON.stringify(document)}`
    );
  }
  public updateOneModel(filter: ModelFilter, document: Model): Observable<number> {
    return this.http.post<number>(
      this.END_POINT + this.UPDATE + MongoCollection.MODEL_COLLECTION_NAME,
      `{ filter: ${JSON.stringify(filter)}, document: ${JSON.stringify(document)}`
    );
  }

  /**
   * Delete one methods (the observable contains the result status number 0-1)
   */
  public deleteOneNormalizedQuery(filter: NormalizedQueryFilter): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.DELETE + MongoCollection.NORMALIZED_QUERY_COLLECTION_NAME, filter);
  }
  public deleteOneQuery(filter: QueryFilter): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.DELETE + MongoCollection.QUERY_COLLECTION_NAME, filter);
  }
  public deleteOneEntity(filter: EntityFilter): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.DELETE + MongoCollection.ENTITY_COLLECTION_NAME, filter);
  }
  public deleteOneEntityHistory(filter: EntityHistoryFilter): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.DELETE + MongoCollection.ENTITY_HISTORY_COLLECTION_NAME, filter);
  }
  public deleteOneModel(filter: ModelFilter): Observable<number> {
    return this.http.post<number>(this.END_POINT + this.DELETE + MongoCollection.MODEL_COLLECTION_NAME, filter);
  }
}
