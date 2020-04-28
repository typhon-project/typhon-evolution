import {Component, Input, OnInit} from '@angular/core';
import {NgbdNavDynamicComponent} from '../navigation/navigation.component';

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

  public chartType = 'pie';

  public chartDatasets: Array<any> = [
    {data: [300, 50, 100, 40, 120], label: 'My First dataset'}
  ];

  public chartLabels: Array<any> = ['Red', 'Green', 'Yellow', 'Grey', 'Dark Grey'];

  public chartColors: Array<any> = [
    {
      backgroundColor: this.getColors(this.size),
      hoverBackgroundColor: this.getColors(this.size + 1).slice(1),
      borderWidth: 2,
    }
  ];

  public chartOptions: any = {
    responsive: true
  };

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


  ngOnInit() {
    this.navigationTab.charts.push(this);
  }


  public randomDatasets() {
    this.chartDatasets = [
      {data: [100, 20, 40, 10, 100], label: 'My second dataset'} ];
  }

}
