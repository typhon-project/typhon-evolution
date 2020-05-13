import {Component, Input, OnInit} from '@angular/core';
import {MongoApiClientService} from '../../services/api/mongo.api.client.service';

@Component({
  selector: 'app-query-details',
  templateUrl: './query-details.component.html',
  styleUrls: ['./query-details.component.scss']
})
export class QueryDetailsComponent implements OnInit {
  @Input() public normalizedQueryUUID;
  @Input() public qlQueryUUID;

  public latestExecutionDate: string;
  public latestExecutionTime: number;
  public queryType: string;
  public concernedEntities: string[];
  public joinEntities: string;
  public implicitInsertedEntities: string[];

  constructor(private mongoApiClientService: MongoApiClientService) { }

  ngOnInit(): void {
    if (this.qlQueryUUID && this.qlQueryUUID != null) {
      this.mongoApiClientService.getNormalizedQueryId(this.qlQueryUUID)
        .subscribe(queries => {
          if (queries && queries != null && (queries as any[]).length === 1) {
            this.normalizedQueryUUID = queries[0].normalizedQueryId;
            this.loadLatestExecutedQuery();
          }
        });
    } else {
      this.loadLatestExecutedQuery();
    }

  }

  private loadLatestExecutedQuery() {

    this.mongoApiClientService.getLatestExecutedQuery(this.normalizedQueryUUID)
      .subscribe(queries => {
        console.log(queries.length);
        if (queries.length !== 1) {
          return;
        }

        const query = queries[0];
        this.latestExecutionDate = new Date(query.executionDate).toLocaleString();
        this.latestExecutionTime = query.executionTime;
        this.queryType = query.type;
        this.concernedEntities = query.allEntities ? query.allEntities : '-';


        const joins = query.joins;
        if (joins) {
          let i = 0;
          this.joinEntities = '';
          for (const join of joins) {
            if (i > 0) {
              this.joinEntities += ', ';
            }
            this.joinEntities += '(' + join.entity1 + '<->' + join.entity2 + ')';
            i++;
          }
        } else {
          this.joinEntities = '-';
        }

        this.implicitInsertedEntities = query.implicitInsertedEntities ? query.implicitInsertedEntities : '-';

      });


  }
}
