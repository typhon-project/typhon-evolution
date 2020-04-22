import {Component} from '@angular/core';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styles: [`
    .close {
      font-size: 1.4rem;
      opacity: 0.1;
      transition: opacity 0.3s;
    }
    .nav-link:hover > .close {
      opacity: 0.8;
    }
    .tab-content {
      padding: 20px 20px;
    }
  `]
})
export class NgbdNavDynamicComponent {
  ENTITY_OBJECT = 0;
  CRUD_OBJECT = 1;

  tabs = [1, 2, 3, 4, 5];
  counter = this.tabs.length + 1;
  active;
  timeEvolutionMode = false;
  public charts: Array<any> = [];

  close(event: MouseEvent, toRemove: number) {
    this.tabs = this.tabs.filter(id => id !== toRemove);
    event.preventDefault();
    event.stopImmediatePropagation();
  }

  add(event: MouseEvent) {
    this.tabs.push(this.counter++);
    event.preventDefault();
  }

  changeChartMode() {
    this.timeEvolutionMode = !this.timeEvolutionMode;
  }

  filterCharts(fromDate: Date, toDate: Date) {
    console.log('filter:' + fromDate + ' ' + toDate);

    this.charts.forEach( (chart) => {
      /*TODO remplacer par l'appel au WS */
      chart.randomDatasets();
    });

  }

  loadCompleteHistory() {
    console.log('complete history');

    this.charts.forEach( (chart) => {
      /*TODO remplacer par l'appel au WS */
      chart.randomDatasets();
    });
  }

  getNavigationComponent() {
    return this;
  }

  openEntityTab(entityName: string) {
    this.tabs.push(this.counter++);
    /*TODO call WS*/
  }
}
