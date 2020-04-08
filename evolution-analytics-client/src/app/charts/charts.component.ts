import {Component, Input, OnChanges} from '@angular/core';

@Component({
  selector: 'app-charts',
  templateUrl: './charts.component.html',
  styleUrls: ['./charts.component.scss']
})
export class ChartsComponent implements OnChanges {

  @Input() userData: number[];
  @Input() orderData: number[];

  public chartType = 'bar';

  public chartDatasets: Array<any> = [
    {data: [423, 473, 523, 573, 623, 673, 723], label: 'User (document database)'},
    {data: [463, 513, 563, 613, 663, 713, 1200], label: 'Order (relational database)'}
  ];

  public chartLabels: Array<any> = ['200 MB', '250 MB', '300 MB', '350 MB', '400 MB', '450 MB', '500 MB'];

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

  constructor() {
  }

  ngOnChanges(changes) {
    console.log(changes);
    this.updateData(this.userData, this.orderData);
  }

  public chartClicked(e: any): void {
  }

  public chartHovered(e: any): void {
  }

  private updateData(userData: number[], orderData: number[]) {
    this.chartDatasets = [
      {data: userData, label: 'User (document database)'},
      {data: orderData, label: 'Order (relational database)'}
    ];
  }
}
