import { Component, OnInit, Input } from '@angular/core';
import { AID } from 'src/app/model/AID.model';
import { Performative } from 'src/app/model/Performative.model';
import { AgentType } from 'src/app/model/AgentType.model';
import { AgentCenter } from 'src/app/model/AgentCenter.model';


@Component({
  selector: 'app-running-agents-listing',
  templateUrl: './running-agents-listing.component.html',
  styleUrls: ['./running-agents-listing.component.css']
})
export class RunningAgentsListingComponent implements OnInit {
  runningAgents: Array<AID>;
  performatives: Array<Performative>;
  fiteredRunningAgents: Array<AID>;

  filterName: string;
  filterType: string;

  constructor() {
    this.runningAgents = new Array();
    this.performatives = new Array();
    this.filterName = '';
    this.filterType = '';
    let type = new AgentType('test', 'test');
    let type2 = new AgentType('test2', 'test');

    let host =  new AgentCenter('localhost', '127.0.0.1');

    let agent = new AID('test', type, host, 'test@localhost');
    let agent2 = new AID('test2', type2, host, 'test2@localhost');
    this.runningAgents.push(agent);
    this.runningAgents.push(agent2);

  }

  ngOnInit() {
    this.fiteredRunningAgents = this.runningAgents;
  }

  onSearchName(){
    this.filterType = '';
    this.fiteredRunningAgents = this.runningAgents.filter(agent => agent.name.includes(this.filterName));
  }

  onSearchType(){
    this.filterName = '';
    this.fiteredRunningAgents = this.runningAgents.filter(agent => agent.type.name.includes(this.filterType));
  }
}
