import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {D3ChartsComponent} from './d3-charts.component';



@NgModule({
  declarations: [D3ChartsComponent],
  imports: [
    CommonModule
  ],
  exports: [
    D3ChartsComponent
  ]
})
export class D3ChartsModule { }
