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

  openQueryDetails(id) {
    console.log('query id:' + id + '=>' + this.randomInt(0, 10));
  }

  randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  private getMostFrequentQueries() {
    const res = [];
    for (let i = 1; i <= 10; i++) {
      const occu = this.randomInt(0, 10);
      res.push({ position: i, id: 'ID_' + this.randomInt(0, 1000), occ: occu, query: 'Last ' + i, handle: 'Handle ' + i });
    }

    return res;
  }

  private getSlowestQueries() {
    const res = [];
    for (let i = 1; i <= 10; i++) {
      const occu = this.randomInt(0, 10);
      res.push({ position: i, id: 'ID_' + this.randomInt(0, 1000), occ: occu, query: 'Last ' + i, handle: 'Handle ' + i });
    }

    return res;
  }

  loadParticularPeriod(fromDate: Date, toDate: Date) {
    this.load(fromDate.getTime(), toDate.getTime());
  }

  loadCompleteHistory() {
    this.load(0, Number.MAX_SAFE_INTEGER);
  }

  load(fromDate: number, toDate: number) {
    if (this.type === this.MOST_FREQUENT) {

      this.mongoApiClientService.getMostFrequentQueries(fromDate, toDate, this.limit)
        .subscribe(queries => {
          console.log(JSON.stringify(queries));
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
      this.mongoApiClientService.getSlowestQueries(fromDate, toDate, this.limit)
        .subscribe(queries => {
          console.log(JSON.stringify(queries));
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

