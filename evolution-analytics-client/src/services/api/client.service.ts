import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {NormalizedQuery} from 'evolution-analytics-model/dist/NormalizedQuery';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  END_POINT = 'http://localhost:3000/';
  FIND = 'find/';
  HTTP_OPTIONS = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) {
  }

  public findAllNormalizedQuery(collectionName: string): Observable<NormalizedQuery[]> {
    return this.http.get<NormalizedQuery[]>(this.END_POINT + this.FIND + collectionName, this.HTTP_OPTIONS);
  }
}
