import {Component, OnInit} from '@angular/core';
import {SocketioService} from '../services/socket/socketio.service';
import {NormalizedQueryFilter} from 'evolution-analytics-model/dist/filter/NormalizedQueryFilter';
import {NormalizedQuery} from 'evolution-analytics-model/dist/model/NormalizedQuery';
import {QueryFilter} from 'evolution-analytics-model/dist/filter/QueryFilter';
import {Query} from 'evolution-analytics-model/dist/model/Query';
import {EntityFilter} from 'evolution-analytics-model/dist/filter/EntityFilter';
import {Entity} from 'evolution-analytics-model/dist/model/Entity';
import {EntityHistoryFilter} from 'evolution-analytics-model/dist/filter/EntityHistoryFilter';
import {EntityHistory} from 'evolution-analytics-model/dist/model/EntityHistory';
import {ModelFilter} from 'evolution-analytics-model/dist/filter/ModelFilter';
import {Model} from 'evolution-analytics-model/dist/model/Model';
import {Selector} from 'evolution-analytics-model/dist/model/Selector';
import {MongoApiClientService} from '../services/api/mongo.api.client.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss', '../../node_modules/bootstrap/scss/bootstrap.scss']
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
    this.mongoApiClientService.findOneNormalizedQuery('5e8f0cbd7ccb4924f78ccb4c').subscribe(normalizedQuery => {
      if (normalizedQuery) {
        console.log(`Normalized query: ${normalizedQuery.normalizedForm}`);
      }
    });
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
    normalizedQueryFilter.normalizedForm = 'updateReviewx0wherex0.id=="?"set{id:"?"}';
    this.mongoApiClientService.findWithFilterNormalizedQueries(normalizedQueryFilter).subscribe(results => {
      if (results) {
        results.forEach(
          result => console.log(`Normalized query displayable form: ${result.displayableForm}`)
        );
      }
    });
    // const queryFilter: QueryFilter = new QueryFilter();
    // queryFilter.query = 'from OrderProduct x0 select x0 where x0.id == "ZWLPj0f1w"';
    // this.mongoApiClientService.findWithFilterQueries(queryFilter).subscribe(queries => {
    //   if (queries) {
    //     queries.forEach(query => console.log(`Query type: ${query.type}`));
    //   }
    // });
    // const entityFilter: EntityFilter = new EntityFilter();
    // entityFilter.name = 'Review';
    // this.mongoApiClientService.findWithFilterEntities(entityFilter).subscribe(entities => {
    //   if (entities) {
    //     entities.forEach(entity => console.log(`Entity lastest version: ${entity.latestVersion}`));
    //   }
    // });
    // const entityHistoryFilter: EntityHistoryFilter = new EntityHistoryFilter();
    // entityHistoryFilter.nbOfQueries = 208;
    // this.mongoApiClientService.findWithFilterEntitiesHistories(entityHistoryFilter).subscribe(entitiesHistories => {
    //   if (entitiesHistories) {
    //     entitiesHistories.forEach(entityHistory => console.log(`Entity history name: ${entityHistory.name}`));
    //   }
    // });
    // const modelFilter: ModelFilter = new ModelFilter();
    // modelFilter.version = 1;
    // this.mongoApiClientService.findWithFilterModels(modelFilter).subscribe(models => {
    //   if (models) {
    //     models.forEach(model => console.log(`Model date: ${model.date}`));
    //   }
    // });
    // const normalizedQuery: NormalizedQuery = new NormalizedQuery(
    //   'fromOrderProductx0selectx0wherex0.id=="?"',
    //   'from OrderProduct x0 select x0 where x0.id == "?"',
    //   3
    // );
    // this.mongoApiClientService.insertOneNormalizedQuery(normalizedQuery).subscribe(result => {
    //   if (result) {
    //     console.log(`insertOneNormalizedQuery result: ${JSON.stringify(result)}`);
    //   }
    // });
    // const query: Query = new Query(
    //   '5e8f0cbd7ccb4924f78ccb4c',
    //   'from OrderProduct x0 select x0 where x0.id == "ZWLPj0f1w"',
    //   'SELECT',
    //   new Date(),
    //   new Date(),
    //   1,
    //   ['OrderProduct'],
    //   [new Selector('OrderProduct', 'id', 'WHERE')]
    // );
    // this.mongoApiClientService.insertOneQuery(query).subscribe(result => {
    //   if (result) {
    //     console.log(`insertOneQuery result: ${JSON.stringify(result)}`);
    //   }
    // });
    // const entity: Entity = new Entity('abodart', 1, [1]);
    // this.mongoApiClientService.insertOneEntity(entity).subscribe(result => {
    //   if (result) {
    //     console.log(`insertOneEntity result: ${JSON.stringify(result)}`);
    //   }
    // });
    // const entityHistory: EntityHistory = new EntityHistory(
    //   'abodart',
    //   new Date(),
    //   1,
    //   0,
    //   17,
    //   3,
    //   7,
    //   5,
    //   2);
    // this.mongoApiClientService.insertOneEntityHistory(entityHistory).subscribe(result => {
    //   if (result) {
    //     console.log(`insertOneEntityHistory result: ${JSON.stringify(result)}`);
    //   }
    // });
    // const model: Model = new Model(1, new Date());
    // this.mongoApiClientService.insertOneModel(model).subscribe(result => {
    //   if (result) {
    //     console.log(`insertOneModel result: ${JSON.stringify(result)}`);
    //   }
    // });
    // const normalizedQueries: NormalizedQuery[] = [new NormalizedQuery(
    //   'fromOrderProductx0selectx0wherex0.id=="?"',
    //   'from OrderProduct x0 select x0 where x0.id == "?"',
    //   3
    // )];
    // this.mongoApiClientService.insertManyNormalizedQueries(normalizedQueries).subscribe(result => {
    //   if (result) {
    //     console.log(`insertManyNormalizedQueries result: ${JSON.stringify(result)}`);
    //   }
    // });
    // const queries: Query[] = [new Query(
    //   '5e8f0cbd7ccb4924f78ccb4c',
    //   'from OrderProduct x0 select x0 where x0.id == "ZWLPj0f1w"',
    //   'SELECT',
    //   new Date(),
    //   new Date(),
    //   1,
    //   ['OrderProduct'],
    //   [new Selector('OrderProduct', 'id', 'WHERE')]
    // )];
    // this.mongoApiClientService.insertManyQueries(queries).subscribe(result => {
    //   if (result) {
    //     console.log(`insertManyQueries result: ${JSON.stringify(result)}`);
    //   }
    // });
    // const entities: Entity[] = [new Entity('abodart', 1, [1])];
    // this.mongoApiClientService.insertManyEntities(entities).subscribe(result => {
    //   if (result) {
    //     console.log(`insertManyEntities result: ${JSON.stringify(result)}`);
    //   }
    // });
    // const entitiesHistories: EntityHistory[] = [new EntityHistory(
    //   'abodart',
    //   new Date(),
    //   1,
    //   0,
    //   17,
    //   3,
    //   7,
    //   5,
    //   2
    // )];
    // this.mongoApiClientService.insertManyEntitiesHistories(entitiesHistories).subscribe(result => {
    //   if (result) {
    //     console.log(`insertManyEntitiesHistories result: ${JSON.stringify(result)}`);
    //   }
    // });
    // const models: Model[] = [new Model(1, new Date())];
    // this.mongoApiClientService.insertManyModels(models).subscribe(result => {
    //   if (result) {
    //     console.log(`insertManyModels result: ${JSON.stringify(result)}`);
    //   }
    // });
    // const normalizedQueryFilter: NormalizedQueryFilter = new NormalizedQueryFilter();
    // normalizedQueryFilter.normalizedForm = 'fromOrderProductx0selectx0wherex0.id=="?"';
    // const normalizedQueryUpdate: NormalizedQueryFilter = new NormalizedQueryFilter();
    // normalizedQueryUpdate.count = 5;
    // this.mongoApiClientService.updateOneNormalizedQuery(normalizedQueryFilter, normalizedQueryUpdate).subscribe(result => {
    //   if (result) {
    //     console.log(`${result.modifiedCount} document(s) updated`);
    //   }
    // }, error => {
    //   console.log(error);
    // });
    // const queryFilter: QueryFilter = new QueryFilter();
    // queryFilter.query = 'from OrderProduct x0 select x0 where x0.id == "ZWLPj0f1w"';
    // this.mongoApiClientService.findWithFilterQueries(queryFilter).subscribe(queries => {
    //   if (queries) {
    //     queries.forEach(query => console.log(`Query type: ${query.type}`));
    //   }
    // });
    // const entityFilter: EntityFilter = new EntityFilter();
    // entityFilter.name = 'Review';
    // this.mongoApiClientService.findWithFilterEntities(entityFilter).subscribe(entities => {
    //   if (entities) {
    //     entities.forEach(entity => console.log(`Entity lastest version: ${entity.latestVersion}`));
    //   }
    // });
    // const entityHistoryFilter: EntityHistoryFilter = new EntityHistoryFilter();
    // entityHistoryFilter.nbOfQueries = 208;
    // this.mongoApiClientService.findWithFilterEntitiesHistories(entityHistoryFilter).subscribe(entitiesHistories => {
    //   if (entitiesHistories) {
    //     entitiesHistories.forEach(entityHistory => console.log(`Entity history name: ${entityHistory.name}`));
    //   }
    // });
    // const modelFilter: ModelFilter = new ModelFilter();
    // modelFilter.version = 1;
    // this.mongoApiClientService.findWithFilterModels(modelFilter).subscribe(models => {
    //   if (models) {
    //     models.forEach(model => console.log(`Model date: ${model.date}`));
    //   }
    // });
    normalizedQueryFilter.normalizedForm = 'fromOrderProductx0selectx0wherex0.id=="?"';
    this.mongoApiClientService.deleteOneNormalizedQuery(normalizedQueryFilter).subscribe(result => {
      if (result) {
        console.log(`${result.deletedCount} document(s) deleted`);
      }
    }, error => {
      console.log(error);
    });
    const queryFilter: QueryFilter = new QueryFilter();
    queryFilter.query = 'from OrderProduct x0 select x0 where x0.id == "ZWLPj0f1w"';
    this.mongoApiClientService.deleteOneQuery(queryFilter).subscribe(result => {
      if (result) {
        console.log(`${result.deletedCount} document(s) deleted`);
      }
    }, error => {
      console.log(error);
    });
    const entityFilter: EntityFilter = new EntityFilter();
    entityFilter.name = 'Review';
    this.mongoApiClientService.deleteOneEntity(entityFilter).subscribe(result => {
      if (result) {
        console.log(`${result.deletedCount} document(s) deleted`);
      }
    }, error => {
      console.log(error);
    });
    const entityHistoryFilter: EntityHistoryFilter = new EntityHistoryFilter();
    entityHistoryFilter.nbOfQueries = 208;
    this.mongoApiClientService.deleteOneEntityHistory(entityHistoryFilter).subscribe(result => {
      if (result) {
        console.log(`${result.deletedCount} document(s) deleted`);
      }
    }, error => {
      console.log(error);
    });
    const modelFilter: ModelFilter = new ModelFilter();
    modelFilter.version = 1;
    this.mongoApiClientService.deleteOneModel(modelFilter).subscribe(result => {
      if (result) {
        console.log(`${result.deletedCount} document(s) deleted`);
      }
    }, error => {
      console.log(error);
    });
  }
}
