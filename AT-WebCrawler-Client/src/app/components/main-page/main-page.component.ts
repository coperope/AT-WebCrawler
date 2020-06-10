import { Component, OnInit } from '@angular/core';

import { AgentType } from 'src/app/model/AgentType.model';
import { AID } from 'src/app/model/AID.model';
import { Performative } from 'src/app/model/Performative.model';
import { AgentCenter } from 'src/app/model/AgentCenter.model';
import { MainServiceService } from 'src/app/services/main-service.service';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {

  agentTypes: Array<AgentType>;
  runningAgents: Array<AID>;
  performatives: Array<Performative>;

  constructor(private mainService: MainServiceService) {
    this.runningAgents = new Array();
    this.performatives = new Array();

    let type = new AgentType('test', 'test');
    let type2 = new AgentType('test2', 'test');

    let host =  new AgentCenter('localhost', '127.0.0.1');

    //let agent = new AID('test', type, host, 'test@localhost');
    //let agent2 = new AID('test2', type2, host, 'test2@localhost');
    //this.runningAgents.push(agent);
    //this.runningAgents.push(agent2);

    this.getAgentTypes();
  }

  ngOnInit() {
  }


  getAgentTypes(){
    this.mainService.getAgentTypes().subscribe(
      (data: Array<AgentType>) => {
        this.agentTypes = data;
      },
      (error) => {
        alert(error);
      }
    );
  }
}
