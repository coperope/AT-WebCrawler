import { Component, OnInit } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';

import { AgentType } from 'src/app/model/AgentType.model';
import { AID } from 'src/app/model/AID.model';
import { WSMessage } from 'src/app/model/WSMessage.model';

import { Performative } from 'src/app/model/Performative.model';
import { MainServiceService } from 'src/app/services/main-service.service';
import { environment } from '../../../environments/environment';

const API_URL = environment.API_URL;

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {
  websocket = webSocket(this.getWebsocketUrl());

  agentTypes: Array<AgentType>;
  runningAgents: Array<AID>;
  performatives: Array<Performative>;
  logs: string;

  constructor(private mainService: MainServiceService) {
    this.runningAgents = new Array();
    this.performatives = new Array();
    this.logs = '';
    this.getAgentTypes();
    this.getRunningAgents();
    this.getPerformatives();

    this.websocket.asObservable().subscribe(
      data => {
        const message = data as WSMessage;
        if (message.type === 'Log') {
          this.logs += message.log + '\n';
        } else if (message.type === 'AgentsTypes') {
          this.agentTypes = message.agentsTypes;

        } else if (message.type === 'ActiveAgents') {
          this.runningAgents = message.activeAgents;

        }
      },
      err => console.log(err),
      () => console.log('WS connection closed')
    );
  }

  ngOnInit() {
  }


  getAgentTypes() {
    this.mainService.getAgentTypes().subscribe(
      (data: Array<AgentType>) => {
        this.agentTypes = data;
      },
      (error) => {
        alert(error);
      }
    );
  }

  getRunningAgents() {
    this.mainService.getRunningAgents().subscribe(
      (data: Array<AID>) => {
        this.runningAgents = data;
      },
      (error) => {
        alert(error);
      }
    );
  }

  getPerformatives() {
    this.mainService.getPerformatives().subscribe(
      (data: Array<Performative>) => {
        this.performatives = data;
      },
      (error) => {
        alert(error);
      }
    );
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
