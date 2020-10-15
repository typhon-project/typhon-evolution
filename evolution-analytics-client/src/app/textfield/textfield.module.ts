import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MDBBootstrapModule} from 'angular-bootstrap-md';
import { FormsModule } from '@angular/forms';
import {TextfieldComponent} from './textfield.component';
import {PieChartComponent} from '../pie-chart/pie-chart.component';
import {TextFieldModule} from '@angular/cdk/text-field';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {MatButtonModule } from '@angular/material/button';
import {MatRadioModule} from '@angular/material/radio';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';



@NgModule({
  declarations: [TextfieldComponent],
  imports: [
    CommonModule,
    FormsModule,
    MDBBootstrapModule.forRoot(),
    TextFieldModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatRadioModule,
    MatSlideToggleModule,
    MatProgressSpinnerModule
  ],
  exports: [
    TextfieldComponent
  ],
  bootstrap: [PieChartComponent]
})
export class TextfieldModule { }
