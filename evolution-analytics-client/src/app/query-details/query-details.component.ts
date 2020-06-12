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
    this.recommendations = this.sanitizer.bypassSecurityTrustHtml('<div class="loader"></div>');
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

  public copyClipboard(event) {
    let res: Array<string> = [];

    const target = event.target || event.srcElement || event.currentTarget;

    const recommendationList: Element = target.closest('.modal-content').getElementsByClassName('recommendationList').item(0);
    if (recommendationList) {
      const firstRecommendationDiv: Element = recommendationList.firstElementChild;

      res = this.getRecommendationsFromRecommendationDiv(firstRecommendationDiv);

    }

    console.log('copied: ' + res);

    const selBox = document.createElement('textarea');
    selBox.style.position = 'fixed';
    selBox.style.left = '0';
    selBox.style.top = '0';
    selBox.style.opacity = '0';
    let content = 'changeOperators  [';
    let i = 0;
    for (const str of res) {
      if ( i > 0) {
        content += ',';
      }

      content += '\n   ' + str;
      i++;
    }
    if ( i > 0) {
      content += '\n';
    }
    content += ']';

    selBox.value = content;
    document.body.appendChild(selBox);
    selBox.focus();
    selBox.select();
    document.execCommand('copy');
    document.body.removeChild(selBox);

  }

  private getRecommendationsFromRecommendationDiv(firstRecommendationDiv: Element) {
    let res: Array<string> = [];

    if (firstRecommendationDiv) {
      const recommendations: HTMLCollection = firstRecommendationDiv.children;
      let i = 0;
      while ( i < recommendations.length) {
        const div: Element = recommendations.item(i);
        const input: any = div.firstElementChild;
        const inputValue: string = input.getAttribute('value');
        const checked = input.checked;
        if (input.getAttribute('type') === 'hidden' || (checked && checked === true)) {
          const recommendationStr = input.nodeValue;
          if (inputValue && inputValue !== '') {
            res.push(inputValue);
          }
          const subRecommendationList: Element = input.nextSibling.nextSibling;
          const res2: Array<string> = this.getRecommendationsFromRecommendationDiv(subRecommendationList);
          res = res.concat(res2);

        }

        i++;
      }
    }

    return res;
  }

}
