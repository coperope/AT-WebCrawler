import { AgentType } from './AgentType.model';
import { AgentCenter } from './AgentCenter.model';

export class AID {
  name: string;
  type: AgentType;
  host: AgentCenter;
  str: string;

  constructor(){
  }
  // constructor(name: string, type: AgentType, host: AgentCenter, str: string){
  //   this.name = name;
  //   this.type = type;
  //   this.host = host;
  //   this.str = str;
  // }
}
