import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {TablePaginationComponent} from './query-table.component';
import {MDBBootstrapModule} from 'angular-bootstrap-md';
import { FormsModule } from '@angular/forms';



@NgModule({
  declarations: [TablePaginationComponent],
  imports: [
    CommonModule,
    FormsModule,
    MDBBootstrapModule.forRoot()
  ],
  exports: [
    TablePaginationComponent
  ]
})
export class QueryTableModule { }
