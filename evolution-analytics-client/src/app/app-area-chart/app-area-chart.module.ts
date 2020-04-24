import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AreaChartComponent} from './app-area-chart.component';



@NgModule({
  declarations: [AreaChartComponent],
  imports: [
    CommonModule
  ],
  exports: [
    AreaChartComponent
  ]
})
export class AppAreaChartModule { }
