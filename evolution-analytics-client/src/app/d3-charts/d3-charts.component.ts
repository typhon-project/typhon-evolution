import {Component, Input, OnInit} from '@angular/core';

import * as d3 from 'd3';
import {randomInt} from '../navigation/navigation.component';
import {MongoApiClientService} from '../../services/api/mongo.api.client.service';
import {SocketioService} from '../../services/socket/socketio.service';

@Component({
  selector: 'app-d3-charts',
  templateUrl: './d3-charts.component.html',
  styleUrls: ['./d3-charts.component.scss']
})
export class D3ChartsComponent implements OnInit {
  @Input() screenPercentage: number;

  constructor(private mongoApiClientService: MongoApiClientService) {
  }

  ngOnInit() {
    const width = window.innerWidth * (this.screenPercentage / 100);

    document.getElementById('typhonSchema').setAttribute('width', width + '');
    document.getElementById('typhonSchema').setAttribute('height', width + '');




    this.drawCirclePacking();
  }

  color(depth: number) {
    if (depth < 0) {
      return 'transparent';
    }

    if (depth === 1) {
      return 'blue';
    }

    if (depth === 2) {
      return 'red';
    }
    return 'black';

  }

  drawCirclePacking() {


    const svg = d3.select('svg');
    const margin = 20;
    const diameter = +svg.attr('width');
    const g = svg.append('g')
      .attr('transform', 'translate(' + diameter / 2 + ',' + diameter / 2 + ')');



    const pack = d3.pack()
      .size([diameter - margin, diameter - margin])
      .padding(2);

    const tooltip = d3.select('app-d3-charts')
      .append('div')
      .style('position', 'absolute')
      .style('z-index', '10')
      .style('visibility', 'hidden')
      .style('margin-left', '20px');



    this.mongoApiClientService.getDatabaseSchema().subscribe(schema => {

      for (const db of schema) {
       db.children = db.entities;
       delete db.entities;
       for (const table of db.children) {
         table.size = table.size + 1;
       }
      }

      const data = {name: 'test', children: schema};

      this.createChart(data, pack, g, tooltip, svg, margin, diameter);
    });


  }


  private createChart(data, pack, g, tooltip, svg, margin, diameter) {
    const root = d3.hierarchy(data)
      .sum((d: any) => d.size)
      .sort((a, b) => b.value - a.value);

    let focus = root;
    const nodes = pack(root).descendants();
    let view = null;

    const circle = g.selectAll('circle')
      .data(nodes)
      .enter().append('circle')
      .attr('class', d => d.parent ? d.children ? 'node' : 'node node--leaf' : 'node node--root')
      .style('fill', d => d.children ? 'transparent' : 'rgb(179, 236, 255)')
      .on('click', d => {
        if (focus !== d) {
          zoom(d);
          d3.event.stopPropagation();
        }
      })
      .on('mouseover', (d: any) => {
        let name: string;
        name = d.data.name;
        if (d.data.size) {
          name += ' (' + (d.data.size - 1) + ' records)';
        }

        if (d.data.type) {
          name += ' (' + d.data.type + ')';
        }


        return tooltip.text(name).style('visibility', 'visible');
      })
      .on('mouseout', () => {
        return tooltip.style('visibility', 'hidden');
      });

    const text = g.selectAll('text')
      .data(nodes)
      .enter().append('text')
      .attr('class', 'label')
      .style('fill-opacity', d => d.parent === root ? 1 : 0)
      .style('display', d => d.parent === root ? 'inline' : 'none')
      .text((d: any) => d.data.name);

    const node = g.selectAll('circle, text');

    svg
      .style('background', 'transparent')
      .on('click', () => zoom(root));

    zoomTo([(root as d3.HierarchyCircularNode<any>).x,
      (root as d3.HierarchyCircularNode<any>).y,
      (root as d3.HierarchyCircularNode<any>).r * 2 + margin]);

    function zoom(d) {
      console.log(d);
      console.log(d.data.size);
      const focus0 = focus;
      focus = d;

      const transition = d3.transition('zoomTransition')
        .duration(d3.event.altKey ? 7500 : 750)
        .tween('zoom', () => {
          const i = d3.interpolateZoom(view, [(focus as d3.HierarchyCircularNode<unknown>).x,
            (focus as d3.HierarchyCircularNode<unknown>).y,
            (focus as d3.HierarchyCircularNode<unknown>).r * 2 + margin]);
          return (t) => { zoomTo(i(t)); };
        });




      transition.selectAll('text')
        .filter(function(n) {

          return (n === focus && (n as any).data.size)
            || (n as d3.HierarchyCircularNode<unknown>).parent === focus || (this as any).style.display === 'inline';
        })
        .style('fill-opacity', n => (n as d3.HierarchyCircularNode<unknown>).parent === focus ||
        (n === focus && (n as any).data.size) ? 1 : 0)
        .on('start', function(n) {
          if ((n as d3.HierarchyCircularNode<unknown>).parent === focus || (n === focus && (n as any).data.size)) {
            (this as any).style.display = 'inline';
          }
        })
        .on('end', function(n) {
          if ((n as d3.HierarchyCircularNode<unknown>).parent !== focus && (n !== focus || !(n as any).data.size)) {
            (this as any).style.display = 'none';
          }
        });
    }




    function zoomTo(v) {
      const k = diameter / v[2];
      view = v;
      node.attr('transform', d => 'translate('
        + ((d as d3.HierarchyCircularNode<unknown>).x - v[0]) * k + ','
        + ((d as d3.HierarchyCircularNode<unknown>).y - v[1]) * k + ')' );
      circle.attr('r', d => d.r * k);
    }
  }
}
