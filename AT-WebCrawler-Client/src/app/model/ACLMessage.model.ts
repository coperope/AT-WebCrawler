import { Performative } from './Performative.model';
import { AID } from './AID.model';

export class ACLMessage {
  performative: string;
  sender: AID;
  receivers: Array<AID>;
  content: string;

  constructor() {
    this.receivers = new Array<AID>();
    this.sender = new AID();
  }
}
