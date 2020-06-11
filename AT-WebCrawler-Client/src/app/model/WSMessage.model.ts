import { AID } from './AID.model';
import { AgentType } from './AgentType.model';

export class WSMessage {
  type: string;
  log: string;
  activeAgents: Array<AID>;
  agentsTypes: Array<AgentType>;

}