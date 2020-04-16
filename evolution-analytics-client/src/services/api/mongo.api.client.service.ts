import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {NormalizedQuery} from 'evolution-analytics-model/dist/NormalizedQuery';
import {Query} from 'evolution-analytics-model/dist/Query';
import {Entity} from 'evolution-analytics-model/dist/Entity';
import {EntityHistory} from 'evolution-analytics-model/dist/EntityHistory';
import {Model} from 'evolution-analytics-model/dist/Model';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MongoApiClientService {

  END_POINT = environment.SOCKET_ENDPOINT;
  /*Resources*/
  FIND = '/find/';
  /*Collections*/
  NORMALIZED_QUERY_COLLECTION = 'QLNormalizedQuery';
  QUERY_COLLECTION = 'QLQuery';
  ENTITY_COLLECTION = 'TyphonEntity';
  ENTITY_HISTORY_COLLECTION = 'TyphonEntityHistory';
  MODEL_COLLECTION = 'TyphonModel';

  constructor(private http: HttpClient) {
  }

  public findAllNormalizedQueries(): Observable<NormalizedQuery[]> {
    return this.http.get<NormalizedQuery[]>(this.END_POINT + this.FIND + this.NORMALIZED_QUERY_COLLECTION);
  }
  public findAllQueries(): Observable<Query[]> {
    return this.http.get<Query[]>(this.END_POINT + this.FIND + this.QUERY_COLLECTION);
  }
  public findAllEntities(): Observable<Entity[]> {
    return this.http.get<Entity[]>(this.END_POINT + this.FIND + this.ENTITY_COLLECTION);
  }
  public findAllEntitiesHistories(): Observable<EntityHistory[]> {
    return this.http.get<EntityHistory[]>(this.END_POINT + this.FIND + this.ENTITY_HISTORY_COLLECTION);
  }
  public findAllModels(): Observable<Model[]> {
    return this.http.get<Model[]>(this.END_POINT + this.FIND + this.MODEL_COLLECTION);
  }
}
