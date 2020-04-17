import {Injectable} from '@angular/core';
import lookup from 'socket.io-client';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SocketioService {
  private socket;

  constructor() {
  }

  setupSocketConnection() {
    this.setSocket(lookup(environment.BACKEND_ENDPOINT));
    this.socket.emit('client-message', 'Hello from Angular web client');
    this.socket.on('server-message', (msg: string) => {
      console.log('Message from the server: ' + msg);
    });
    this.socket.on('chart-update', (data: number[]) => {
      console.log('Message from the server: ' + data);
    });
  }

  getSocket(): any {
    return this.socket;
  }

  setSocket(socket: any) {
    this.socket = socket;
  }
}
