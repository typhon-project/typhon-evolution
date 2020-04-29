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

  constructor(private mongoApiClientService: MongoApiClientService, private socketService: SocketioService) {
  }

  ngOnInit() {
    this.socketService.setupSocketConnection();
    // this.testMongoApiClientService();
      }

  private testMongoApiClientService() {
    this.mongoApiClientService.findAllNormalizedQueries().subscribe(findAllNormalizedQueries => {
      if (findAllNormalizedQueries) {
        findAllNormalizedQueries.forEach(findAllNormalizedQuery =>
          console.log(`Normalized query: ${findAllNormalizedQuery.normalizedForm}`));
      }
    });
    this.mongoApiClientService.findAllQueries().subscribe(findAllQueries => {
      if (findAllQueries) {
        findAllQueries.forEach(findAllQuery => console.log(`Query: ${findAllQuery.query}`));
      }
    });
    this.mongoApiClientService.findAllEntities().subscribe(findAllEntities => {
      if (findAllEntities) {
        findAllEntities.forEach(findAllEntity => console.log(`Entity name: ${findAllEntity.name}`));
      }
    });
    this.mongoApiClientService.findAllEntitiesHistories().subscribe(findAllEntitiesHistories => {
      if (findAllEntitiesHistories) {
        findAllEntitiesHistories.forEach(findAllEntityHistory =>
          console.log(`Entity history nbOfQueries: ${findAllEntityHistory.nbOfQueries}`));
      }
    });
    this.mongoApiClientService.findAllModels().subscribe(findAllModels => {
      if (findAllModels) {
        findAllModels.forEach(findAllModel => console.log(`Model version: ${findAllModel.version}`));
      }
    });
    this.mongoApiClientService.findOneNormalizedQuery('5e8f0cbd7ccb4924f78ccb4c').subscribe(findOneNormalizedQuery => {
      if (findOneNormalizedQuery) {
        console.log(`Normalized query: ${findOneNormalizedQuery.normalizedForm}`);
      }
    });
    this.mongoApiClientService.findOneQuery('5e8f0cbd7ccb4924f78ccb4d').subscribe(findOneQuery => {
      if (findOneQuery) {
        console.log(`Query: ${findOneQuery.query}`);
      }
    });
    this.mongoApiClientService.findOneEntity('5e8f0ca27ccb4924f78ccb3f').subscribe(findOneEntity => {
      if (findOneEntity) {
        console.log(`Entity name: ${findOneEntity.name}`);
      }
    });
    this.mongoApiClientService.findOneEntityHistory('5e8f0ca47ccb4924f78ccb46').subscribe(findOneEntityHistory => {
      if (findOneEntityHistory) {
        console.log(`Entity history nbOfQueries: ${findOneEntityHistory.nbOfQueries}`);
      }
    });
    this.mongoApiClientService.findOneModel('5e8f0ca27ccb4924f78ccb3e').subscribe(findOneModel => {
      if (findOneModel) {
        console.log(`Model version: ${findOneModel.version}`);
      }
    });
    const findWithFilterNormalizedQueriesFilter: NormalizedQueryFilter = new NormalizedQueryFilter();
    findWithFilterNormalizedQueriesFilter.normalizedForm = 'updateReviewx0wherex0.id=="?"set{id:"?"}';
    this.mongoApiClientService.findWithFilterNormalizedQueries(findWithFilterNormalizedQueriesFilter).subscribe(
      findWithFilterNormalizedQueries => {
        if (findWithFilterNormalizedQueries) {
          findWithFilterNormalizedQueries.forEach(findWithFilterNormalizedQuery =>
            console.log(`Normalized query displayable form: ${findWithFilterNormalizedQuery.displayableForm}`)
          );
        }
      });
    const findWithFilterQueriesFilter: QueryFilter = new QueryFilter();
    findWithFilterQueriesFilter.query = 'from OrderProduct x0 select x0 where x0.id == "ZWLPj0f1w"';
    this.mongoApiClientService.findWithFilterQueries(findWithFilterQueriesFilter).subscribe(findWithFilterQueries => {
      if (findWithFilterQueries) {
        findWithFilterQueries.forEach(findWithFilterQuery => console.log(`Query type: ${findWithFilterQuery.type}`));
      }
    });
    const findWithFilterEntitiesFilter: EntityFilter = new EntityFilter();
    findWithFilterEntitiesFilter.name = 'Review';
    this.mongoApiClientService.findWithFilterEntities(findWithFilterEntitiesFilter).subscribe(findWithFilterEntities => {
      if (findWithFilterEntities) {
        findWithFilterEntities.forEach(findWithFilterEntity =>
          console.log(`Entity lastest version: ${findWithFilterEntity.latestVersion}`));
      }
    });
    const findWithFilterEntitiesHistoriesFilter: EntityHistoryFilter = new EntityHistoryFilter();
    findWithFilterEntitiesHistoriesFilter.nbOfQueries = 208;
    this.mongoApiClientService.findWithFilterEntitiesHistories(findWithFilterEntitiesHistoriesFilter).subscribe(
      findWithFilterEntitiesHistories => {
        if (findWithFilterEntitiesHistories) {
          findWithFilterEntitiesHistories.forEach(findWithFilterEntityHistory =>
            console.log(`Entity history name: ${findWithFilterEntityHistory.name}`));
        }
      });
    const findWithFilterModelsFilter: ModelFilter = new ModelFilter();
    findWithFilterModelsFilter.version = 1;
    this.mongoApiClientService.findWithFilterModels(findWithFilterModelsFilter).subscribe(findWithFilterModels => {
      if (findWithFilterModels) {
        findWithFilterModels.forEach(findWithFilterModel => console.log(`Model date: ${findWithFilterModel.date}`));
      }
    });
    const normalizedQuery: NormalizedQuery = new NormalizedQuery(
      'fromOrderProductx0selectx0wherex0.id=="?"',
      'from OrderProduct x0 select x0 where x0.id == "?"',
      3
    );
    this.mongoApiClientService.insertOneNormalizedQuery(normalizedQuery).subscribe(insertOneNormalizedQuery => {
      if (insertOneNormalizedQuery) {
        console.log(`insertOneNormalizedQuery result: ${JSON.stringify(insertOneNormalizedQuery)}`);
      }
    });
    const query: Query = new Query(
      '5e8f0cbd7ccb4924f78ccb4c',
      'from OrderProduct x0 select x0 where x0.id == "ZWLPj0f1w"',
      'SELECT',
      new Date(),
      new Date(),
      1,
      ['OrderProduct'],
      [new Selector('OrderProduct', 'id', 'WHERE')]
    );
    this.mongoApiClientService.insertOneQuery(query).subscribe(insertOneQuery => {
      if (insertOneQuery) {
        console.log(`insertOneQuery result: ${JSON.stringify(insertOneQuery)}`);
      }
    });
    const entity: Entity = new Entity('abodart', 1, 'DocumentDatabase', '', [1]);
    this.mongoApiClientService.insertOneEntity(entity).subscribe(insertOneEntity => {
      if (insertOneEntity) {
        console.log(`insertOneEntity result: ${JSON.stringify(insertOneEntity)}`);
      }
    });
    const entityHistory: EntityHistory = new EntityHistory(
      'abodart',
      new Date(),
      1,
      0,
      17,
      3,
      7,
      5,
      2);
    this.mongoApiClientService.insertOneEntityHistory(entityHistory).subscribe(insertOneEntityHistory => {
      if (insertOneEntityHistory) {
        console.log(`insertOneEntityHistory result: ${JSON.stringify(insertOneEntityHistory)}`);
      }
    });
    const model: Model = new Model(1, new Date());
    this.mongoApiClientService.insertOneModel(model).subscribe(insertOneModel => {
      if (insertOneModel) {
        console.log(`insertOneModel result: ${JSON.stringify(insertOneModel)}`);
      }
    });
    const normalizedQueries: NormalizedQuery[] = [new NormalizedQuery(
      'fromOrderProductx0selectx0wherex0.id=="?"',
      'from OrderProduct x0 select x0 where x0.id == "?"',
      3
    )];
    this.mongoApiClientService.insertManyNormalizedQueries(normalizedQueries).subscribe(insertManyNormalizedQueries => {
      if (insertManyNormalizedQueries) {
        console.log(`insertManyNormalizedQueries result: ${JSON.stringify(insertManyNormalizedQueries)}`);
      }
    });
    const queries: Query[] = [new Query(
      '5e8f0cbd7ccb4924f78ccb4c',
      'from OrderProduct x0 select x0 where x0.id == "ZWLPj0f1w"',
      'SELECT',
      new Date(),
      new Date(),
      1,
      ['OrderProduct'],
      [new Selector('OrderProduct', 'id', 'WHERE')]
    )];
    this.mongoApiClientService.insertManyQueries(queries).subscribe(insertManyQueries => {
      if (insertManyQueries) {
        console.log(`insertManyQueries result: ${JSON.stringify(insertManyQueries)}`);
      }
    });
    const entities: Entity[] = [new Entity('abodart', 1, 'DocumentDatabase', '', [1])];
    this.mongoApiClientService.insertManyEntities(entities).subscribe(insertManyEntities => {
      if (insertManyEntities) {
        console.log(`insertManyEntities result: ${JSON.stringify(insertManyEntities)}`);
      }
    });
    const entitiesHistories: EntityHistory[] = [new EntityHistory(
      'abodart',
      new Date(),
      1,
      0,
      17,
      3,
      7,
      5,
      2
    )];
    this.mongoApiClientService.insertManyEntitiesHistories(entitiesHistories).subscribe(insertManyEntitiesHistories => {
      if (insertManyEntitiesHistories) {
        console.log(`insertManyEntitiesHistories result: ${JSON.stringify(insertManyEntitiesHistories)}`);
      }
    });
    const models: Model[] = [new Model(1, new Date())];
    this.mongoApiClientService.insertManyModels(models).subscribe(insertManyModels => {
      if (insertManyModels) {
        console.log(`insertManyModels result: ${JSON.stringify(insertManyModels)}`);
      }
    });
    const normalizedQueryFilter: NormalizedQueryFilter = new NormalizedQueryFilter();
    normalizedQueryFilter.normalizedForm = 'fromOrderProductx0selectx0wherex0.id=="?"';
    const normalizedQueryUpdate: NormalizedQueryFilter = new NormalizedQueryFilter();
    normalizedQueryUpdate.count = 5;
    this.mongoApiClientService.updateOneNormalizedQuery(normalizedQueryFilter, normalizedQueryUpdate).subscribe(result => {
      if (result) {
        console.log(`${result.modifiedCount} document(s) updated`);
      }
    }, error => {
      console.log(error);
    });
    const updateOneQueryFilter: QueryFilter = new QueryFilter();
    updateOneQueryFilter.query = 'from CreditCard x0 select x0 where x0.number == "8"';
    const updateOneQueryUpdate: QueryFilter = new QueryFilter();
    updateOneQueryUpdate.executionDate = new Date();
    this.mongoApiClientService.updateOneQuery(updateOneQueryFilter, updateOneQueryUpdate).subscribe(result => {
      if (result) {
        console.log(`${result.modifiedCount} document(s) updated`);
      }
    });
    const updateOneEntityFilter: EntityFilter = new EntityFilter();
    updateOneEntityFilter.name = 'Product';
    const updateOneEntityUpdate: EntityFilter = new EntityFilter();
    updateOneEntityUpdate.latestVersion = 2;
    this.mongoApiClientService.updateOneEntity(updateOneEntityFilter, updateOneEntityUpdate).subscribe(result => {
      if (result) {
        console.log(`${result.modifiedCount} document(s) updated`);
      }
    });
    const updateOneEntityHistoryFilter: EntityHistoryFilter = new EntityHistoryFilter();
    updateOneEntityHistoryFilter.name = 'User';
    const updateOneEntityHistoryUpdate: EntityHistoryFilter = new EntityHistoryFilter();
    updateOneEntityHistoryUpdate.nbOfQueries = 167;
    this.mongoApiClientService.updateOneEntityHistory(updateOneEntityHistoryFilter, updateOneEntityHistoryUpdate).subscribe(result => {
      if (result) {
        console.log(`${result.modifiedCount} document(s) updated`);
      }
    });
    const updateOneModelFilter: ModelFilter = new ModelFilter();
    updateOneModelFilter.version = 1;
    const updateOneModelUpdate: ModelFilter = new ModelFilter();
    updateOneModelUpdate.date = new Date();
    this.mongoApiClientService.updateOneModel(updateOneModelFilter, updateOneModelUpdate).subscribe(result => {
      if (result) {
        console.log(`${result.modifiedCount} document(s) updated`);
      }
    });
    const deleteOneNormalizedQueryFilter: NormalizedQueryFilter = new NormalizedQueryFilter();
    deleteOneNormalizedQueryFilter.normalizedForm = 'fromOrderProductx0selectx0wherex0.id=="?"';
    this.mongoApiClientService.deleteOneNormalizedQuery(deleteOneNormalizedQueryFilter).subscribe(deleteOneNormalizedQuery => {
      if (deleteOneNormalizedQuery) {
        console.log(`${deleteOneNormalizedQuery.deletedCount} document(s) deleted`);
      }
    }, error => {
      console.log(error);
    });
    const deleteOneQueryFilter: QueryFilter = new QueryFilter();
    deleteOneQueryFilter.query = 'from OrderProduct x0 select x0 where x0.id == "ZWLPj0f1w"';
    this.mongoApiClientService.deleteOneQuery(deleteOneQueryFilter).subscribe(deleteOneQuery => {
      if (deleteOneQuery) {
        console.log(`${deleteOneQuery.deletedCount} document(s) deleted`);
      }
    }, error => {
      console.log(error);
    });
    const deleteOneEntityFilter: EntityFilter = new EntityFilter();
    deleteOneEntityFilter.name = 'Review';
    this.mongoApiClientService.deleteOneEntity(deleteOneEntityFilter).subscribe(deleteOneEntity => {
      if (deleteOneEntity) {
        console.log(`${deleteOneEntity.deletedCount} document(s) deleted`);
      }
    }, error => {
      console.log(error);
    });
    const deleteOneEntityHistoryFilter: EntityHistoryFilter = new EntityHistoryFilter();
    deleteOneEntityHistoryFilter.nbOfQueries = 208;
    this.mongoApiClientService.deleteOneEntityHistory(deleteOneEntityHistoryFilter).subscribe(deleteOneEntityHistory => {
      if (deleteOneEntityHistory) {
        console.log(`${deleteOneEntityHistory.deletedCount} document(s) deleted`);
      }
    }, error => {
      console.log(error);
    });
    const deleteOneModelFilter: ModelFilter = new ModelFilter();
    deleteOneModelFilter.version = 1;
    this.mongoApiClientService.deleteOneModel(deleteOneModelFilter).subscribe(deleteOneModel => {
      if (deleteOneModel) {
        console.log(`${deleteOneModel.deletedCount} document(s) deleted`);
      }
    }, error => {
      console.log(error);
    });
  }
}
