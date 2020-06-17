import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { webSocket } from 'rxjs/webSocket';
import { Property } from 'src/app/model/Property.model';
import { WSMessage } from 'src/app/model/WSMessage.model';
import { environment } from '../../../environments/environment';
@Component({
  selector: 'app-realestate',
  templateUrl: './realestate.component.html',
  styleUrls: ['./realestate.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class RealestateComponent implements OnInit {
  websocket = webSocket(this.getWebsocketUrl());
  dataTop100: Property[];
  dataLocation: Property[];
  constructor() {
    this.websocket.asObservable().subscribe(
      data => {
        const message = data as WSMessage;
        if (message.type === 'Top100') {
          this.dataTop100 = message.top100;
          console.log(this.dataTop100);
        }else if(message.type === 'TopLocations'){
          this.dataLocation = message.topLocations;
          console.log(this.dataLocation);
        }
      },
      err => console.log(err),
      () => console.log('WS connection closed')
    );
   }

  ngOnInit() {
  }
  getWebsocketUrl() {
    var loc = window.location, new_uri;
    if (loc.protocol === 'https:') {
      new_uri = 'wss:';
    } else {
      new_uri = 'ws:';
    }
    new_uri += '//' + loc.host;
    new_uri += loc.pathname + 'ws';
    //console.log(new_uri);
    return environment.production ? new_uri : environment.WS_URL;
  }
}
