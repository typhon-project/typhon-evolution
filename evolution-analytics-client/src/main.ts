import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';

if (environment.production) {
  enableProdMode();
}

console.log('process.env.BACKEND_ENDPOINT: ' + process.env.BACKEND_ENDPOINT);
console.log('environment.BACKEND_ENDPOINT: ' + environment.BACKEND_ENDPOINT);

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
