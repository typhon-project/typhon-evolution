import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {QueryDetailsComponent} from './query-details.component';
import {IconsModule} from 'angular-bootstrap-md';
import { ButtonsModule, WavesModule, CollapseModule } from 'angular-bootstrap-md';



@NgModule({
  declarations: [QueryDetailsComponent],
  imports: [
    CommonModule,
    IconsModule,
    ButtonsModule,
    WavesModule,
    CollapseModule
  ],
  exports: [
    QueryDetailsComponent
  ]
})
export class QueryDetailsModule { }
