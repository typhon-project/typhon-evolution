import {Component, Input} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {NgbdNavDynamicComponent} from '../navigation/navigation.component';

/**
 * @title Radios with ngModel
 */
@Component({
  selector: 'app-time-radio-button',
  templateUrl: 'timeradiobutton.component.html',
  styleUrls: ['timeradiobutton.component.scss'],
})
export class TimeradiobuttonComponent {
  static readonly COMPLETE_HISTORY: string = '1';
  static readonly PARTICULAR_PERIOD: string = '2';

  periodMode: string;
  particularPeriodFilterVisible = false;
  fromBoundDate: Date;
  toBoundDate: Date;

  @Input() navigationTab: NgbdNavDynamicComponent;

  changePeriodMode() {
    if (this.periodMode === TimeradiobuttonComponent.PARTICULAR_PERIOD) {
      this.loadParticularPeriodFilter();
    } else {
        this.particularPeriodFilterVisible = false;
        if (this.periodMode === TimeradiobuttonComponent.COMPLETE_HISTORY) {
          this.navigationTab.loadCompleteHistory();
        }
    }
  }

  private loadParticularPeriodFilter() {
    this.particularPeriodFilterVisible = true;
  }

  filter() {
    this.navigationTab.filterCharts(this.fromBoundDate, this.toBoundDate);
  }

  changeChartMode() {
    this.navigationTab.changeChartMode();
  }

}
