import { Discipline } from './discipline.model';
import { Event } from './event.model';
import { Athlete } from './athlete.model';

export interface Results {
  id: number;
  athlete: Athlete;
  discipline: Discipline;
  event: Event;
  value: number;
}
