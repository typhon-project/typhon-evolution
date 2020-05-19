import {Component, Input, OnInit} from '@angular/core';
import {NgbdNavDynamicComponent} from '../navigation/navigation.component';
import {MongoApiClientService} from '../../services/api/mongo.api.client.service';

@Component({
  selector: 'app-line-chart',
  templateUrl: './line-chart.component.html',
  styleUrls: ['./line-chart.component.scss'],
})
export class LineChartComponent  implements OnInit {
  @Input() public chartTitle: string;
  @Input() private navigationTab: NgbdNavDynamicComponent;
  @Input() public objectType: number;
  @Input() public size = 10;
  @Input() public type;
  @Input() public chartsId;
  @Input() public entityName;
  @Input() public qlQueryUUID: string;
  @Input() public normalizedQueryUUID: string;

  public chartType = 'line';

  public chartDatasets: Array<any> = [
    { data: [65, 59, 80, 81, 56, 55, 40], label: 'My First dataset' },
    { data: [28, 48, 40, 19, 86, 27, 90], label: 'My Second dataset' }
  ];

  public chartLabels: Array<any> = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];

  public chartColors: Array<any> = [
    {
      backgroundColor: 'rgba(105, 0, 132, .2)',
      borderColor: 'rgba(200, 99, 132, .7)',
      borderWidth: 2,
    },
    {
      backgroundColor: 'rgba(0, 137, 132, .2)',
      borderColor: 'rgba(0, 10, 130, .7)',
      borderWidth: 2,
    }
  ];

  public chartOptions: any = {
    responsive: true
  };

  static SIZE(): string { return 'SIZE'; }
  static NBOFQUERIES(): string {return 'NB_OF_QUERIES'; }

  public chartClicked(e: any): void { }
  public chartHovered(e: any): void { }


  constructor(private mongoApiClientService: MongoApiClientService) {
  }

  getColor(size: number, i: number, opacity: boolean) {
    const colors = [];
    const minGreen = 30;
    const maxGreen = 95;
    const interval = Math.round((maxGreen - minGreen) / size);
    const green = 30 + (interval * i);
    if (opacity) {
      return 'hsla(194, 100%, ' + green + '%, 50%)';
    } else {
      return 'hsl(194, 100%, ' + green + '%)';
    }


  }

  getChartColors() {
    const colors = [];
    let i = 0;
    while (i < this.chartDatasets.length) {
      colors.push({
        backgroundColor: this.getColor(this.chartDatasets.length, i, true),
          borderColor: this.getColor(this.chartDatasets.length + 1, i, false),
        borderWidth: 2,
      });
      i++;
    }


    return colors;


  }

  ngOnInit() {
    this.navigationTab.addChart(this, this.chartsId);
    this.loadCompleteHistory();
  }

  loadParticularPeriod(fromDate: Date, toDate: Date) {
    this.load(this.entityName, fromDate.getTime(), toDate.getTime());
  }

  loadCompleteHistory() {
    this.load(this.entityName, -1, -1);
  }

  load(entityName: string, fromDate: number, toDate: number) {

    if (this.objectType === this.navigationTab.CRUD_OBJECT) {
      this.chartDatasets = [];
      this.chartLabels = [];

      this.mongoApiClientService.getCRUDOperationDistributionOverTime(entityName, fromDate, toDate, this.size).subscribe(cruds => {

        const datasets: Array<any> = [
          { data: [], label: 'Select' },
          { data: [], label: 'Delete' },
          { data: [], label: 'Insert' },
          { data: [], label: 'Update' }
        ];

        let labels = [];

        if (cruds != null && cruds.length === 1) {
          labels = this.transformDate(cruds[0].time);
          const values: any[] = cruds[0].values;
          if (values) {
            for (const value of values) {
              datasets[0].data.push(value.selects);
              datasets[1].data.push(value.deletes);
              datasets[2].data.push(value.inserts);
              datasets[3].data.push(value.updates);
            }
          }

          this.chartDatasets = datasets;
          this.chartLabels = labels;

          this.chartColors = this.getChartColors();
        }

      });

    } else {

      if (this.type === LineChartComponent.SIZE() &&
        this.objectType === this.navigationTab.ENTITY_OBJECT) {

        this.chartDatasets = [];
        this.chartLabels = [];

        this.mongoApiClientService.getEntitiesSizePeriodOverTime(entityName, fromDate, toDate, this.size).subscribe(sizes => {
          console.log('sizes:' + JSON.stringify(sizes));

          const datasets: Array<any> = [];

          const dateArray: number[] = sizes.dates;
          const labels = this.transformDate(dateArray);

          const entityArray = sizes.entities;

          for (const entity of entityArray) {
            datasets.push({data: entity.history, label: entity.entityName});
          }

          this.chartDatasets = datasets;
          this.chartLabels = labels;

          this.chartColors = this.getChartColors();

        });


      } else {

        if (this.type === LineChartComponent.NBOFQUERIES() &&
          this.objectType === this.navigationTab.ENTITY_OBJECT) {
          this.chartDatasets = [];
          this.chartLabels = [];

          this.mongoApiClientService.getQueriedEntitiesPeriodOverTime(entityName, fromDate, toDate, this.size).subscribe(entities => {
            const datasets: Array<any> = [];

            const dateArray: number[] = entities.dates;
            const labels = this.transformDate(dateArray);

            const entityArray = entities.entities;

            for (const entity of entityArray) {
              datasets.push({data: entity.history, label: entity.entityName});
            }

            this.chartDatasets = datasets;
            this.chartLabels = labels;

            this.chartColors = this.getChartColors();

          });
        } else {
          if (this.objectType === this.navigationTab.QUERY_OBJECT) {

            if (fromDate === -1) {
              fromDate = 0;
            }

            if (toDate === -1) {
               toDate = Number.MAX_SAFE_INTEGER;
            }

            this.chartDatasets = [];
            this.chartLabels = [];

            this.mongoApiClientService.getQueryExecutionTimeOverTime(this.normalizedQueryUUID,
              this.qlQueryUUID, fromDate, toDate).subscribe(times => {



                const datasets: Array<any> = [{data: [], label: 'Time in ms'}];
                const dateArray: number[] = [];

                for (const time of times) {
                  const executionDate = time.executionDate;
                  const executionTime = time.executionTime;
                  dateArray.push(executionDate);
                  datasets[0].data.push(executionTime);
                }

                const labels = this.transformDate(dateArray);

                this.chartDatasets = datasets;
                this.chartLabels = labels;

                this.chartColors = this.getChartColors();
            });

          }
        }
      }
    }
  }

  private transformDate(dates: number[]) {
    let intervalMS = 0;
    if (dates.length > 1) {
      const date1 = dates[0];
      const date2 = dates[1];
      intervalMS = date2 - date1;
    }

    const res = [];

    if (intervalMS >= (24 * 60 * 60 * 1000)) {
      // diff more than one day
      let i = 0;
      while (i < dates.length) {
        const date = new Date(dates[i]);
        res.push(date.toLocaleDateString());
        i++;
      }
    } else {
      // less than one day
      let i = 0;
      while (i < dates.length) {
        const date = new Date(dates[i]);
        if (i === 0) {
          res.push(date.toLocaleString());
        } else {
          res.push(date.toLocaleTimeString());
        }
        i++;
      }
    }


    return res;

  }
}
