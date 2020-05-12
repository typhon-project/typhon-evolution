import {Component, OnInit, ViewChild, HostListener, AfterViewInit, ChangeDetectorRef, Input} from '@angular/core';
import { MdbTablePaginationComponent, MdbTableDirective } from 'angular-bootstrap-md';
import {MongoApiClientService} from '../../services/api/mongo.api.client.service';
import {NgbdNavDynamicComponent} from '../navigation/navigation.component';

@Component({
  selector: 'app-table-pagination',
  templateUrl: './query-table.component.html',
  styleUrls: ['./query-table.component.scss']
})
export class TablePaginationComponent implements OnInit, AfterViewInit  {
  @Input() public type: number;
  @Input() public limit = 50;
  @Input() public chartTitle: string;
  @Input() public chartsId: string;
  @Input() private navigationTab: NgbdNavDynamicComponent;
  @Input() private secondColumnName: string;
  @Input() private entityName: string;

  @ViewChild(MdbTablePaginationComponent, { static: true }) mdbTablePagination: MdbTablePaginationComponent;
  @ViewChild(MdbTableDirective, { static: true }) mdbTable: MdbTableDirective;

  elements: any = [];
  titleElements = ['position', 'occ.', 'query', ''];
  headElements = ['position', 'occ', 'query', 'handle'];
  searchText = '';
  previous: string;

  MOST_FREQUENT = 0;
  SLOWEST = 1;

  @HostListener('input') oninput() {
    this.searchItems();
  }

  constructor(private cdRef: ChangeDetectorRef, private mongoApiClientService: MongoApiClientService) { }

  ngOnInit() {
    this.titleElements[1] = this.secondColumnName;
    this.navigationTab.addChart(this, this.chartsId);
    this.loadCompleteHistory();

  }

  ngAfterViewInit() {
    this.mdbTablePagination.setMaxVisibleItemsNumberTo(5);

    this.mdbTablePagination.calculateFirstItemIndex();
    this.mdbTablePagination.calculateLastItemIndex();
    this.cdRef.detectChanges();
  }

  searchItems() {
    const prev = this.mdbTable.getDataSource();
    if (!this.searchText) {
      this.mdbTable.setDataSource(this.previous);
      this.elements = this.mdbTable.getDataSource();
    }

    if (this.searchText) {
      this.elements = this.mdbTable.searchLocalDataBy(this.searchText);
      this.mdbTable.setDataSource(prev);
    }
  }

  openQueryDetails(id, query) {
    this.navigationTab.openQueryTab(id, query, this.type);
  }

  loadParticularPeriod(fromDate: Date, toDate: Date) {
    this.load(fromDate.getTime(), toDate.getTime());
  }

  loadCompleteHistory() {
    this.load(0, Number.MAX_SAFE_INTEGER);
  }

  load(fromDate: number, toDate: number) {
    if (this.type === this.MOST_FREQUENT) {

      this.mongoApiClientService.getMostFrequentQueries(this.entityName, fromDate, toDate, this.limit)
        .subscribe(queries => {
          const array = [];
          let i = 0;
          for (const query of queries) {
            array.push({ position: (i + 1), id: query._id, occ: query.count, query: query.query, handle: 'Handle ' + i });
            i++;
          }

          this.elements = array;
          this.mdbTable.setDataSource(this.elements);
          this.elements = this.mdbTable.getDataSource();
          this.previous = this.mdbTable.getDataSource();

        });


    }

    if (this.type === this.SLOWEST) {
      this.mongoApiClientService.getSlowestQueries(this.entityName, fromDate, toDate, this.limit)
        .subscribe(queries => {
          const array = [];
          let i = 0;
          for (const query of queries) {
            array.push({ position: (i + 1), id: query._id, occ: query.executionTime, query: query.query, handle: 'Handle ' + i });
            i++;
          }

          this.elements = array;
          this.mdbTable.setDataSource(this.elements);
          this.elements = this.mdbTable.getDataSource();
          this.previous = this.mdbTable.getDataSource();

        });
    }
  }
}

