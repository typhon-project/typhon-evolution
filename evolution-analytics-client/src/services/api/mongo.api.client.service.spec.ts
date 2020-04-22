import {TestBed} from '@angular/core/testing';
import {MongoApiClientService} from './mongo.api.client.service';

describe('ClientService', () => {
  let mongoApiClientService: MongoApiClientService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    mongoApiClientService = TestBed.inject(MongoApiClientService);
  });

  it('should be created', () => {
    expect(mongoApiClientService).toBeTruthy();
  });
});
