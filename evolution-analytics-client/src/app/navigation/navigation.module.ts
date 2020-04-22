import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {NgbNavModule} from '@ng-bootstrap/ng-bootstrap';
import {NgbdNavDynamicComponent} from './navigation.component';
import {PieChartModule} from '../pie-chart/pie-chart.module';
import {ChartsModule} from '../charts/charts.module';
import {TimeradiobuttonModule} from '../timeradiobutton/timeradiobutton.module';
import {LineChartModule} from '../line-chart/line-chart.module';
import {QueryTableModule} from '../query-table/query-table.module';



@NgModule({
  declarations: [NgbdNavDynamicComponent],
  exports: [
    NgbdNavDynamicComponent
  ],
  imports: [
    CommonModule,
    NgbNavModule,
    PieChartModule,
    ChartsModule,
    TimeradiobuttonModule,
    LineChartModule,
    QueryTableModule
  ]
})
export class NavigationModule { }
