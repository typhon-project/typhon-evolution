import {Component, Input, OnInit} from '@angular/core';
import {NgbdNavDynamicComponent} from '../navigation/navigation.component';
import {AppComponent} from '../app.component';
import {MongoApiClientService} from '../../services/api/mongo.api.client.service';
import {from} from 'rxjs';


@Component({
  selector: 'app-pie-chart',
  templateUrl: './pie-chart.component.html',
  styleUrls: ['./pie-chart.component.scss'],
})
export class PieChartComponent implements OnInit {

  @Input() public chartTitle: string;
  @Input() private navigationTab: NgbdNavDynamicComponent;
  @Input() public objectType: number;
  @Input() public size = 5;
  @Input() public type;


  public chartType = 'pie';

  public chartDatasets: Array<any> = [
    {data: [300, 50, 100, 40, 120], label: 'My First dataset'}
  ];

  public chartLabels: Array<any> = ['Red', 'Green', 'Yellow', 'Grey', 'Dark Grey'];

  public chartColors: Array<any> = this.getChartColors();

  public chartOptions: any = {
    responsive: true
  };

  static SIZE(): string { return 'SIZE'; }
  static NBOFQUERIES(): string {return 'NB_OF_QUERIES'; }

  constructor(private mongoApiClientService: MongoApiClientService) {
  }

  getChartColors() {
    return [
      {
        backgroundColor: this.getColors(this.size),
        hoverBackgroundColor: this.getColors(this.size + 1).slice(1),
        borderWidth: 2,
      }
    ];
  }

  public chartClicked(e: any): void {
    if (e.active.length > 0) {
      const index: number = e.active[0]._index;
      console.log(this.objectType + 'ok');
      if (this.objectType === this.navigationTab.ENTITY_OBJECT) {
        console.log('ici');
        const entityName: string = this.chartLabels[index];
        this.navigationTab.openEntityTab(entityName);
      }

      console.log('Index', e.active[0]._index);
      console.log('Data' , e.active[0]._chart.config.data.datasets[0].data[e.active[0]._index]);
      console.log('Label' , e.active[0]._chart.config.data.labels[e.active[0]._index]);
    }
  }

  public chartHovered(e: any): void {
  }

  getColors(size) {
    const colors = [];
    let i = 0;
    const minGreen = 30;
    const maxGreen = 95;
    const interval = Math.round((maxGreen - minGreen) / size);
    while (i < size) {
      const green = 30 + (interval * i);
      colors.push('hsl(194, 100%, ' + green + '%)');
      i++;
    }
    return colors;
  }

  load(fromDate: number, toDate: number) {

    if (this.objectType === this.navigationTab.CRUD_OBJECT) {
      this.chartDatasets = [{data: [], label: this.chartTitle}];
      this.chartLabels = ['Select', 'Insert', 'Update', 'Delete'];

      this.mongoApiClientService.getCRUDOperationDistribution(fromDate, toDate).subscribe(cruds => {
        console.log('CRUDS:' + JSON.stringify(cruds));
        const datasets = [{data: [], label: this.chartTitle}];

        if (cruds != null && cruds.length === 1) {
          datasets[0].data.push(cruds[0].selects);
          datasets[0].data.push(cruds[0].inserts);
          datasets[0].data.push(cruds[0].updates);
          datasets[0].data.push(cruds[0].deletes);
          console.log(JSON.stringify(datasets));

          this.chartDatasets = datasets;
          this.size = this.chartLabels.length;
          this.chartColors = this.getChartColors();
        }

      });

    } else {

      if (this.type === PieChartComponent.SIZE() &&
        this.objectType === this.navigationTab.ENTITY_OBJECT) {
        this.chartDatasets = [{data: [], label: this.chartTitle}];
        this.chartLabels = [];

        this.mongoApiClientService.getEntitiesSizeByPeriod(fromDate, toDate).subscribe(schema => {
          const datasets = [{data: [], label: this.chartTitle}];
          const labels = [];
          for (const db of schema) {
            for (const entity of db.entities) {
              const entityName = entity.name;
              const entitySize = entity.size;


              if (entitySize > 0) {
                datasets[0].data.push(entitySize);
                labels.push(entityName);
              }
            }
          }

          this.chartDatasets = datasets;
          this.chartLabels = labels;
          this.size = labels.length;
          this.chartColors = this.getChartColors();
        });

      } else {
        if (this.type === PieChartComponent.NBOFQUERIES() &&
          this.objectType === this.navigationTab.ENTITY_OBJECT) {

          this.chartDatasets = [{data: [], label: this.chartTitle}];
          this.chartLabels = [];

          this.mongoApiClientService.getQueriedEntitiesPropertion(fromDate, toDate).subscribe(prop => {

            console.log('QUERIED ENTITIES PROPORTION:' + JSON.stringify(prop));
            const datasets = [{data: [], label: this.chartTitle}];
            const labels = [];

            if (prop != null) {
              for (const entity of prop) {
                const entityName = entity._id;
                const nbOfQueries = entity.nbOfQueries;
                datasets[0].data.push(nbOfQueries);
                labels.push(entityName);
              }
            }

            this.chartDatasets = datasets;
            this.chartLabels = labels;
            this.size = this.chartLabels.length;
            this.chartColors = this.getChartColors();

          });

        }
      }
    }
  }

  loadParticularPeriod(fromDate: Date, toDate: Date) {
    this.load(fromDate.getTime(), toDate.getTime());
  }

  loadCompleteHistory() {
    const fromDate = 0;
    const toDate = Number.MAX_SAFE_INTEGER;
    this.load(fromDate, toDate);
  }


  ngOnInit() {
    this.navigationTab.charts.push(this);
    this.loadCompleteHistory();
  }


  public randomDatasets() {
    this.chartDatasets = [
      {data: [100, 20, 40, 10, 100], label: 'My second dataset'} ];
  }

}
