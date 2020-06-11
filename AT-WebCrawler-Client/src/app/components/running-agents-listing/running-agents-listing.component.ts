import { Component, OnInit, Input, SimpleChanges, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { AID } from 'src/app/model/AID.model';
import { MainServiceService } from 'src/app/services/main-service.service';
import { Performative } from 'src/app/model/Performative.model';
import { ACLMessage } from 'src/app/model/ACLMessage.model';
import { MatTable } from '@angular/material/table';


@Component({
  selector: 'app-running-agents-listing',
  templateUrl: './running-agents-listing.component.html',
  styleUrls: ['./running-agents-listing.component.css']
})
export class RunningAgentsListingComponent implements OnInit {
  @Input() performatives: Array<Performative>;
  @Input() runningAgents: Array<AID>;
  fiteredRunningAgents: Array<AID>;
  displayedColumns: string[] = ['name', 'type', 'host', 'action'];
  @ViewChild('receivers') receivers: Array<AID>;

  filterName: string;
  filterType: string;

  stopAgentAID: AID;
  message: ACLMessage;

  selectedSender: string;
  selectedReceivers: Array<string>;
  selectedPerformative: string;
  content: string;
  @ViewChild(MatTable, { static: true }) table: MatTable<any>;


  constructor(private modalService: NgbModal,
              private mainService: MainServiceService) {
    this.filterName = '';
    this.filterType = '';
    this.message = new ACLMessage();
    this.selectedReceivers = new Array();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.fiteredRunningAgents = this.runningAgents;
    this.filterName = '';
    this.filterType = '';
  }

  ngOnInit() {
    
  }

  onSearchName() {
    this.filterType = '';
    this.fiteredRunningAgents = this.runningAgents.filter(agent =>
      agent.name.toLocaleLowerCase().includes(this.filterName.toLocaleLowerCase()));
  }

  onSearchType() {
    this.filterName = '';
    this.fiteredRunningAgents = this.runningAgents.filter(agent =>
      agent.type.name.toLocaleLowerCase().includes(this.filterType.toLocaleLowerCase()));
  }

  stopAgent(content, agent) {
    this.stopAgentAID = agent;
    this.modalService.open(content, { size: 'sm' });

  }

  stopAgentConfirmed(){
    if (!this.stopAgentAID) {
      return;
    }

    this.mainService.stopAgent(this.stopAgentAID).subscribe(
      (data: Array<AID>) => {
        this.modalService.dismissAll();
        this.stopAgentAID = new AID();
      },
      (error) => {
        alert(error);
      }
    );
  }

  sendMessageModal(content, agent) {
    this.selectedReceivers.splice(0, this.selectedReceivers.length);
    if (agent) {
      this.selectedReceivers.push(agent.str);
    }
    this.modalService.open(content, { });
  }

  sendMessage(){
    this.message.sender = this.runningAgents.filter(agent => agent.str === this.selectedSender)[0];
    this.message.receivers = this.runningAgents.filter(agent => this.selectedReceivers.includes(agent.str));
    this.message.performative = this.selectedPerformative;
    this.message.content = this.content;

    if (!this.message.sender || !this.message.sender || !this.message.sender || !this.message.sender){
      return;
    }

    this.mainService.sendMessage(this.message).subscribe(
      (data: any) => {
        this.modalService.dismissAll();
        this.selectedReceivers.splice(0, this.selectedReceivers.length);
        this.selectedPerformative = undefined;
        this.selectedSender = undefined;
        this.content = undefined;
      },
      (error) => {
        alert(error);
      }
    );

  }
}
