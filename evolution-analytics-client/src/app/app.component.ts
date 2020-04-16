import {Component, OnInit} from '@angular/core';
import {SocketioService} from '../services/socket/socketio.service';
import {ClientService} from '../services/api/client.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'Evolution Analytics';
  userData = [423, 473, 523, 573, 623, 673, 723];
  orderData = [463, 513, 563, 613, 663, 713, 763];

  constructor(private clientService: ClientService, private socketService: SocketioService) {
  }

  ngOnInit() {
    this.socketService.setupSocketConnection();
    this.clientService.findAllNormalizedQuery('QLNormalizedQuery').subscribe(queries => {
      console.log(queries);
      if (queries) {
        queries.forEach(query => console.log(query.normalizedForm));
      }
    });
  }
}
