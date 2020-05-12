import {AfterContentInit, Component, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {AreaChartComponent} from '../app-area-chart/app-area-chart.component';
import * as d3 from 'd3';
import * as uuid from 'uuid';

@Component({
  selector: 'app-navigation',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss'],
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
export class NgbdNavDynamicComponent implements OnInit, AfterContentInit  {
  @ViewChild('areaChart', { static: true }) chart: AreaChartComponent;

  chartData = [];

  CRUD_OBJECT = 1;
  ENTITY_OBJECT = 0;
  QUERY_OBJECT = 2;

  tabs = [];
  counter = this.tabs.length + 1;
  active;
  timeEvolutionMode = false;
  public charts = new Map();

  lastChartsId: string;
  entityTabs = [];
  queryTabs: any[] = [];

  addChart(chart, UUID: string) {
    let array: any[] = this.charts.get(UUID);
    if (!array || array == null) {
      array = [];
      this.charts.set(UUID, array);
    }

    array.push(chart);
  }

  closeEntityTab(event: MouseEvent, toRemove: string) {
    this.entityTabs = this.entityTabs.filter(entityName => entityName !== toRemove);
    event.preventDefault();
    event.stopImmediatePropagation();
  }

  closeQueryTab(event: MouseEvent, toRemove: string) {
    this.queryTabs = this.queryTabs.filter(query => query.uuid !== toRemove);
    event.preventDefault();
    event.stopImmediatePropagation();
  }

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

  filterCharts(UUID: string, fromDate, toDate) {
    const from = new Date(fromDate);
    const to = new Date(toDate);

    const arrays = this.charts.get(UUID);

    if (arrays && arrays.length > 0) {
      arrays.forEach((chart) => {
        chart.loadParticularPeriod(from, to);
      });
    }

  }

  loadCompleteHistory(UUID: string) {
    this.charts.get(UUID).forEach( (chart) => {
      chart.loadCompleteHistory();
    });
  }

  getNavigationComponent() {
    return this;
  }

  openEntityTab(entityName: string) {
    // this.tabs.push(this.counter++);
    this.entityTabs.push(entityName);

    /*TODO call WS*/
  }

  ngOnInit(): void {
  }

  ngAfterContentInit(): void {
    this.generateData();
  }

  generateData() {
    this.chartData = [];
    const meanPrepTime = randomInt(10, 11);
    const meanWaitTime = randomInt(8, 9);
    const meanTransitTime = randomInt(9, 10);

    const meanTotalTime = meanPrepTime + meanWaitTime + meanTransitTime;

    const sigmaPrepTime = randomInt(1, 1);
    const sigmaWaitTime = randomInt(2, 3);
    const sigmaTransitTime = randomInt(1, 2);

    const sigmaTotalTime = Math.floor(
      Math.sqrt(Math.pow(sigmaPrepTime, 2) +
        Math.pow(sigmaWaitTime, 2) +
        Math.pow(sigmaTransitTime, 2))
    );

    const prandomizer = d3.randomNormal(meanPrepTime, sigmaPrepTime);
    const wrandomizer = d3.randomNormal(meanWaitTime, sigmaWaitTime);
    const trandomizer = d3.randomNormal(meanTransitTime, sigmaTransitTime);

    const ptimes = [];
    const wtimes = [];
    const ttimes = [];
    const totaltimes = [];
    for (let i = 0; i < 500; i++) {
      const p = Math.floor(prandomizer());
      const w = Math.floor(wrandomizer());
      const t = Math.floor(trandomizer());
      const total = p + w + t;
      ptimes.push(p);
      wtimes.push(w);
      ttimes.push(t);
      totaltimes.push(total);
    }
    this.chartData.push(ptimes);
    this.chartData.push(wtimes);
    this.chartData.push(ttimes);
    this.chartData.push(totaltimes);
  }

  getTabComp() {
    return this;
  }

  generateNewChartsId() {
    this.lastChartsId = '0000';
    return this.lastChartsId;
  }

  getLastChartsId() {
    return this.lastChartsId;
  }

  openQueryTab(id: string, q: string, queryType: number) {
    this.queryTabs.push( {uuid: id, query: q, type: queryType} );
  }

  formatTabTitle(tabName: string) {
    const lengthLimit = 20;
    if (tabName.length > lengthLimit) {
      tabName = tabName.substring(0, lengthLimit) + '...';
    }

    return tabName;
  }
}

export function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}
