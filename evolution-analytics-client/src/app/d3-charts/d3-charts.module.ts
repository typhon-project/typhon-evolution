import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {D3ChartsComponent} from './d3-charts.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';


@NgModule({
  declarations: [D3ChartsComponent],
  imports: [
    CommonModule,
    MatProgressSpinnerModule
  ],
  exports: [
    D3ChartsComponent
  ]
})
export class D3ChartsModule { }
