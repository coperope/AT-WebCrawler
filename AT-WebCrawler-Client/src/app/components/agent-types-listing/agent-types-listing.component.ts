import { Component, OnInit, Input, SimpleChanges } from '@angular/core';

import { AgentType } from 'src/app/model/AgentType.model';
import { MainServiceService } from 'src/app/services/main-service.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AID } from 'src/app/model/AID.model';

@Component({
  selector: 'app-agent-types-listing',
  templateUrl: './agent-types-listing.component.html',
  styleUrls: ['./agent-types-listing.component.css']
})
export class AgentTypesListingComponent implements OnInit {
  @Input() agentTypes: Array<AgentType>;

  filteredTypes: Array<AgentType>;
  filter: string;

  newAgent: AID;

  constructor(private modalService: NgbModal,
              private mainService: MainServiceService) {
    this.filter = '';
    this.newAgent = new AID();
    this.newAgent.name = '';
  }

  ngOnChanges(changes: SimpleChanges) {
    this.filteredTypes = this.agentTypes;
    this.filter = '';
  }

  ngOnInit() {
    this.filteredTypes = this.agentTypes;
  }

  onSearch() {
    this.filteredTypes = this.agentTypes.filter(type => type.name.toLocaleLowerCase().includes(this.filter.toLocaleLowerCase()));
  }

  openModal(content, type) {
    this.newAgent.type = type;
    this.modalService.open(content, { size: 'sm' });
  }

  createAgent() {
    if (!this.newAgent.name) {
      alert("Agent name is required");
      return;
    }

    if (!this.newAgent.type) {
      return;
    }
    console.log(this.newAgent);

    this.mainService.startAgent(this.newAgent.type, this.newAgent.name).subscribe(
      (data: Array<AgentType>) => {
        //this.newAgent = new AID();
        //this.newAgent.name = '';
      },
      (error) => {
        alert(error);
      }
    );


  }
}
