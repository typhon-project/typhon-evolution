import {CdkTextareaAutosize} from '@angular/cdk/text-field';
import {Component, NgZone, ViewChild, Input} from '@angular/core';
import {take} from 'rxjs/operators';
import {MongoApiClientService} from '../../services/api/mongo.api.client.service';


/** @title Auto-resizing textarea */
@Component({
  selector: 'app-text-field',
  templateUrl: './textfield.component.html',
  styleUrls: ['./textfield.component.scss'],
})
export class TextfieldComponent {
  evolveResponse = '';
  waitingDiv = true;

  constructor(private _ngZone: NgZone, private mongoApiClientService: MongoApiClientService) {}

  @ViewChild('autosize') autosize: CdkTextareaAutosize;

  triggerResize() {
    // Wait for changes to be applied, then trigger textarea resize.
    this._ngZone.onStable.pipe(take(1))
      .subscribe(() => this.autosize.resizeToFitContent(true));
  }

  applyEvolutionOperator(changeOperator: string) {
    console.log('apply:' + changeOperator);
    if (changeOperator && changeOperator.length > 0) {
      this.waitingDiv = false;
      this.mongoApiClientService.applyChangeOperator(changeOperator)
        .subscribe(response => {
          this.evolveResponse = response.toString();
          this.waitingDiv = true;
        });
    } else {
      this.evolveResponse = 'Please enter a change operator';
    }
  }
}
