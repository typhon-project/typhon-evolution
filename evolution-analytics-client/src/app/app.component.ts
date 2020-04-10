import {Component, OnInit} from '@angular/core';
import {SocketioService} from '../services/socketio.service';
import {NormalizedQuery} from 'evolution-analytics-model/dist/NormalizedQuery';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'Evolution Analytics';
  userData = [423, 473, 523, 573, 623, 673, 723];
  orderData = [463, 513, 563, 613, 663, 713, 763];

  constructor(private socketService: SocketioService) {
  }

  ngOnInit() {
    this.socketService.setupSocketConnection();
  }
}
