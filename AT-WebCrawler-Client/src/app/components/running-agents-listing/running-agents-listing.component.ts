import { Component, OnInit, Input, SimpleChanges } from '@angular/core';
import { AID } from 'src/app/model/AID.model';


@Component({
  selector: 'app-running-agents-listing',
  templateUrl: './running-agents-listing.component.html',
  styleUrls: ['./running-agents-listing.component.css']
})
export class RunningAgentsListingComponent implements OnInit {
  @Input() runningAgents: Array<AID>;
  fiteredRunningAgents: Array<AID>;

  filterName: string;
  filterType: string;

  constructor() {
    this.filterName = '';
    this.filterType = '';

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
}
