import {Component, OnInit} from '@angular/core';
import {SocketioService} from '../services/socket/socketio.service';
import {MongoApiClientService} from '../services/api/mongo.api.client.service';
import {NormalizedQueryFilter} from 'evolution-analytics-model/dist/filter/NormalizedQueryFilter';
import {QueryFilter} from 'evolution-analytics-model/dist/filter/QueryFilter';
import {EntityFilter} from 'evolution-analytics-model/dist/filter/EntityFilter';
import {EntityHistoryFilter} from 'evolution-analytics-model/dist/filter/EntityHistoryFilter';
import {ModelFilter} from 'evolution-analytics-model/dist/filter/ModelFilter';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'Evolution Analytics';
  userData = [423, 473, 523, 573, 623, 673, 723];
  orderData = [463, 513, 563, 613, 663, 713, 763];

  constructor(private mongoApiClientService: MongoApiClientService, private socketService: SocketioService) {
  }

  ngOnInit() {
    this.socketService.setupSocketConnection();
    // this.mongoApiClientService.findAllNormalizedQueries().subscribe(normalizedQueries => {
    //   if (normalizedQueries) {
    //     normalizedQueries.forEach(normalizedQuery => console.log(`Normalized query: ${normalizedQuery.normalizedForm}`));
    //   }
    // });
    // this.mongoApiClientService.findAllQueries().subscribe(queries => {
    //   if (queries) {
    //     queries.forEach(query => console.log(`Query: ${query.query}`));
    //   }
    // });
    // this.mongoApiClientService.findAllEntities().subscribe(entities => {
    //   if (entities) {
    //     entities.forEach(entity => console.log(`Entity name: ${entity.name}`));
    //   }
    // });
    // this.mongoApiClientService.findAllEntitiesHistories().subscribe(entitiesHistories => {
    //   if (entitiesHistories) {
    //     entitiesHistories.forEach(entityHistory => console.log(`Entity history nbOfQueries: ${entityHistory.nbOfQueries}`));
    //   }
    // });
    // this.mongoApiClientService.findAllModels().subscribe(models => {
    //   if (models) {
    //     models.forEach(model => console.log(`Model version: ${model.version}`));
    //   }
    // });
    // this.mongoApiClientService.findOneNormalizedQuery('5e8f0cbd7ccb4924f78ccb4c').subscribe(normalizedQuery => {
    //   if (normalizedQuery) {
    //     console.log(`Normalized query: ${normalizedQuery.normalizedForm}`);
    //   }
    // });
    // this.mongoApiClientService.findOneQuery('5e8f0cbd7ccb4924f78ccb4d').subscribe(query => {
    //   if (query) {
    //     console.log(`Query: ${query.query}`);
    //   }
    // });
    // this.mongoApiClientService.findOneEntity('5e8f0ca27ccb4924f78ccb3f').subscribe(entity => {
    //   if (entity) {
    //     console.log(`Entity name: ${entity.name}`);
    //   }
    // });
    // this.mongoApiClientService.findOneEntityHistory('5e8f0ca47ccb4924f78ccb46').subscribe(entityHistory => {
    //   if (entityHistory) {
    //     console.log(`Entity history nbOfQueries: ${entityHistory.nbOfQueries}`);
    //   }
    // });
    // this.mongoApiClientService.findOneModel('5e8f0ca27ccb4924f78ccb3e').subscribe(model => {
    //   if (model) {
    //     console.log(`Model version: ${model.version}`);
    //   }
    // });
    const normalizedQueryFilter: NormalizedQueryFilter = new NormalizedQueryFilter();
    normalizedQueryFilter.normalizedForm = 'fromOrderProductx0selectx0wherex0.id=="?"';
    this.mongoApiClientService.findWithFilterNormalizedQueries(normalizedQueryFilter).subscribe(normalizedQueries => {
      if (normalizedQueries) {
        normalizedQueries.forEach(normalizedQuery => console.log(`Normalized query displayable form: ${normalizedQuery.displayableForm}`));
      }
    });
    const queryFilter: QueryFilter = new QueryFilter();
    queryFilter.query = 'from OrderProduct x0 select x0 where x0.id == "ZWLPj0f1w"';
    this.mongoApiClientService.findWithFilterQueries(queryFilter).subscribe(queries => {
      if (queries) {
        queries.forEach(query => console.log(`Query type: ${query.type}`));
      }
    });
    const entityFilter: EntityFilter = new EntityFilter();
    entityFilter.name = 'Review';
    this.mongoApiClientService.findWithFilterEntities(entityFilter).subscribe(entities => {
      if (entities) {
        entities.forEach(entity => console.log(`Entity lastest version: ${entity.latestVersion}`));
      }
    });
    const entityHistoryFilter: EntityHistoryFilter = new EntityHistoryFilter();
    entityHistoryFilter.nbOfQueries = 208;
    this.mongoApiClientService.findWithFilterEntitiesHistories(entityHistoryFilter).subscribe(entitiesHistories => {
      if (entitiesHistories) {
        entitiesHistories.forEach(entityHistory => console.log(`Entity history name: ${entityHistory.name}`));
      }
    });
    const modelFilter: ModelFilter = new ModelFilter();
    modelFilter.version = 1;
    this.mongoApiClientService.findWithFilterModels(modelFilter).subscribe(models => {
      if (models) {
        models.forEach(model => console.log(`Model date: ${model.date}`));
      }
    });
  }
}
