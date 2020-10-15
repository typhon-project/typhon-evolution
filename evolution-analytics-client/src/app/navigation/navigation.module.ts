import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {NgbNavModule} from '@ng-bootstrap/ng-bootstrap';
import {NgbdNavDynamicComponent} from './navigation.component';
import {PieChartModule} from '../pie-chart/pie-chart.module';
import {ChartsModule} from '../charts/charts.module';
import {TimeradiobuttonModule} from '../timeradiobutton/timeradiobutton.module';
import {LineChartModule} from '../line-chart/line-chart.module';
import {QueryTableModule} from '../query-table/query-table.module';
import {AppAreaChartModule} from '../app-area-chart/app-area-chart.module';
import {D3ChartsModule} from '../d3-charts/d3-charts.module';
import {QueryDetailsModule} from '../query-details/query-details.module';
import {TextfieldModule} from '../textfield/textfield.module';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';



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
    QueryTableModule,
    AppAreaChartModule,
    D3ChartsModule,
    QueryDetailsModule,
    TextfieldModule,
    MatFormFieldModule,
    MatInputModule,
  ]
})
export class NavigationModule { }
