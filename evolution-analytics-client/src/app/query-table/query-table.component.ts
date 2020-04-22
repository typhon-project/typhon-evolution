import { Component, OnInit, ViewChild, HostListener, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { MdbTablePaginationComponent, MdbTableDirective } from 'angular-bootstrap-md';

@Component({
  selector: 'app-table-pagination',
  templateUrl: './query-table.component.html',
  styleUrls: ['./query-table.component.scss']
})
export class TablePaginationComponent implements OnInit, AfterViewInit  {

  @ViewChild(MdbTablePaginationComponent, { static: true }) mdbTablePagination: MdbTablePaginationComponent;
  @ViewChild(MdbTableDirective, { static: true }) mdbTable: MdbTableDirective;

  elements: any = [];
  titleElements = ['position', 'occ.', 'query', ''];
  headElements = ['position', 'occ', 'query', 'handle'];
  searchText = '';
  previous: string;

  @HostListener('input') oninput() {
    this.searchItems();
  }

  constructor(private cdRef: ChangeDetectorRef) { }

  ngOnInit() {
    for (let i = 1; i <= 10; i++) {
      const occu = this.randomInt(0, 10);
      this.elements.push({ position: i, id: 'ID_' + this.randomInt(0, 1000), occ: occu, query: 'Last ' + i, handle: 'Handle ' + i });
    }
    this.mdbTable.setDataSource(this.elements);
    this.elements = this.mdbTable.getDataSource();
    this.previous = this.mdbTable.getDataSource();
  }

  ngAfterViewInit() {
    this.mdbTablePagination.setMaxVisibleItemsNumberTo(5);

    this.mdbTablePagination.calculateFirstItemIndex();
    this.mdbTablePagination.calculateLastItemIndex();
    this.cdRef.detectChanges();
  }

  searchItems() {
    console.log('test:' + this.searchText);
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

}

