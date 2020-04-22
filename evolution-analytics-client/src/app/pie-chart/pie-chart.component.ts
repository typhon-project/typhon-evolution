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

  public chartType = 'pie';

  public chartDatasets: Array<any> = [
    {data: [300, 50, 100, 40, 120], label: 'My First dataset'}
  ];

  public chartLabels: Array<any> = ['Red', 'Green', 'Yellow', 'Grey', 'Dark Grey'];

  public chartColors: Array<any> = [
    {
      backgroundColor: ['#F7464A', '#46BFBD', '#FDB45C', '#949FB1', '#4D5360'],
      hoverBackgroundColor: ['#FF5A5E', '#5AD3D1', '#FFC870', '#A8B3C5', '#616774'],
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


  ngOnInit() {
    this.navigationTab.charts.push(this);
  }


  public randomDatasets() {
    this.chartDatasets = [
      {data: [100, 20, 40, 10, 100], label: 'My second dataset'} ];
  }

}
