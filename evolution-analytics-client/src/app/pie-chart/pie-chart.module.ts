import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MDBBootstrapModule} from 'angular-bootstrap-md';
import {PieChartComponent} from './pie-chart.component';



@NgModule({
  declarations: [PieChartComponent],
  imports: [
    CommonModule,
    MDBBootstrapModule.forRoot()
  ],
  exports: [
    PieChartComponent
  ],
  bootstrap: [PieChartComponent]
})
export class PieChartModule { }
