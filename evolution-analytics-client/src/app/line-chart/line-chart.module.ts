import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {LineChartComponent} from './line-chart.component';
import {PieChartComponent} from '../pie-chart/pie-chart.component';
import {MDBBootstrapModule} from 'angular-bootstrap-md';



@NgModule({
  declarations: [LineChartComponent],
  imports: [
    CommonModule,
    MDBBootstrapModule.forRoot()
  ],
  exports: [
    LineChartComponent
  ],
  bootstrap: [PieChartComponent]
})
export class LineChartModule { }
