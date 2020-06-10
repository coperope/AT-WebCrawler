import { Performative } from './Performative.model';
import { AID } from './AID.model';

export class ACLMessage {
  performative: Performative;
  sender: AID;
  recievers: Array<AID>;
  content: string;

  constructor(performative: Performative, sender: AID, recievers: Array<AID>, content: string) {
    this.performative = performative;
    this.sender = sender;
    this.recievers = recievers;
    this.content = content;
  }
}
