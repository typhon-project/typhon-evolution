import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {TimeradiobuttonComponent} from './timeradiobutton.component';
import {MatRadioModule} from '@angular/material/radio';
import {FormsModule} from '@angular/forms';
import {MatButtonModule } from '@angular/material/button';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';


@NgModule({
  declarations: [TimeradiobuttonComponent],
  imports: [
    CommonModule,
    MatRadioModule,
    FormsModule,
    MatButtonModule,
    MatSlideToggleModule
  ],
  exports: [
    TimeradiobuttonComponent
  ]
})
export class TimeradiobuttonModule { }
