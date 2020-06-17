import { AID } from './AID.model';
import { AgentType } from './AgentType.model';
import { Statistics } from './Statistics.model';
import { Property } from './Property.model';

export class WSMessage {
  type: string;
  log: string;
  activeAgents: Array<AID>;
  agentsTypes: Array<AgentType>;
  top100: Array<Property>;
  topLocations: Array<Property>;
  statistic: Statistics;
}