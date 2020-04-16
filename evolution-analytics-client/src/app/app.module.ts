import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {SocketioService} from '../services/socket/socketio.service';
import {ChartsModule} from './charts/charts.module';
import {ClientService} from '../services/api/client.service';
import {HttpClientModule} from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ChartsModule
  ],
  providers: [ClientService, SocketioService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
