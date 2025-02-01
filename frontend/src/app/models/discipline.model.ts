import { Equipment } from './equipment.model';
import { Athlete } from './athlete.model';
import { Event } from './event.model';
import { Coach } from './coach.model';

export interface Discipline {
  id: number;
  name: string;
  description: string;
  imageLink?: string;
  equipment?: Equipment[];
  athletes?: Athlete[];
  events?: Event[];
  coaches?: Coach[];
}
