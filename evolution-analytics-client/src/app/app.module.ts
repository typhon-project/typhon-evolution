import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {SocketioService} from '../services/socket/socketio.service';
import {MongoApiClientService} from '../services/api/mongo.api.client.service';
import {HttpClientModule} from '@angular/common/http';
import {NavigationModule} from './navigation/navigation.module';
import {MatButtonModule } from '@angular/material/button';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {TextFieldModule} from '@angular/cdk/text-field';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NavigationModule,
    MatButtonModule,
    MatSlideToggleModule,
    TextFieldModule
  ],
  providers: [MongoApiClientService, SocketioService],
  exports: [
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
