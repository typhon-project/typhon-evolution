import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ChartsComponent} from './charts.component';
import {MDBBootstrapModule} from 'angular-bootstrap-md';

@NgModule({
  declarations: [
    ChartsComponent
  ],
  imports: [
    CommonModule,
    MDBBootstrapModule.forRoot()
  ],
  exports: [
    ChartsComponent
  ],
  bootstrap: [ChartsComponent]
})
export class ChartsModule {
}
