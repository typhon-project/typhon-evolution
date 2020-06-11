import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {MongoApiClientService} from '../../services/api/mongo.api.client.service';
import {NgbdNavDynamicComponent} from '../navigation/navigation.component';
import {NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'app-query-details',
  templateUrl: './query-details.component.html',
  styleUrls: ['./query-details.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class QueryDetailsComponent implements OnInit {
  @Input() public normalizedQueryUUID;
  @Input() public qlQueryUUID;
  @Input() public nav: NgbdNavDynamicComponent;

  public latestExecutionDate: string;
  public latestExecutionTime: number;
  public queryType: string;
  @Input() public query: string;
  public concernedEntities: string[];
  public joinEntities: any[];
  public implicitInsertedEntities: string[];

  closeResult = '';
  recommendations: any;


  constructor(private mongoApiClientService: MongoApiClientService, private modalService: NgbModal, private sanitizer: DomSanitizer) { }

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
        this.query = query.displayableForm;
        this.concernedEntities = query.allEntities;

        this.joinEntities = [];
        const joins = query.joins;
        if (joins) {
          for (const join of joins) {
            this.joinEntities.push({e1: join.entity1, e2: join.entity2});
          }
        }

        this.implicitInsertedEntities = query.implicitInsertedEntities;

      });


  }

  openEntityTab(entity: string) {
    this.nav.openEntityTab(entity);
  }

  openRecommendationsPanel(content) {
    const uuid = this.normalizedQueryUUID;
    this.mongoApiClientService.recommend(uuid).subscribe( recommendations => {
      console.log(recommendations);
      this.recommendations = this.sanitizer.bypassSecurityTrustHtml(recommendations);

    });

    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', size: 'xl'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }
}
