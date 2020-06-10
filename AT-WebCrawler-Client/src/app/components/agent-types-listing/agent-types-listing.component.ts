import { Component, OnInit, Input } from '@angular/core';

import { AgentType } from 'src/app/model/AgentType.model';
import { MainServiceService } from 'src/app/services/main-service.service';

@Component({
  selector: 'app-agent-types-listing',
  templateUrl: './agent-types-listing.component.html',
  styleUrls: ['./agent-types-listing.component.css']
})
export class AgentTypesListingComponent implements OnInit {
  agentTypes: Array<AgentType>;
  filteredTypes: Array<AgentType>;

  filter: string;

  constructor(private mainService: MainServiceService) {
    this.filter = '';
    this.agentTypes = new Array();
    let type = new AgentType('test', 'test');
    let type2 = new AgentType('test2', 'test');
    this.agentTypes.push(type);
    this.agentTypes.push(type2);
    this.getAgentTypes();
  }

  ngOnInit() {
    this.filteredTypes = this.agentTypes;
  }

  onSearch(){
    this.filteredTypes = this.agentTypes.filter(type => type.name.includes(this.filter));
  }

  getAgentTypes(){
    this.mainService.getAgentTypes().subscribe(
      (data: any) => {
        this.agentTypes = data;
      },
      (error) => {
        alert(error);
      }
    );
  }

}
