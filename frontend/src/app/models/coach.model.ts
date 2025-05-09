import { Athlete } from './athlete.model';
import { Discipline } from './discipline.model';

export interface Coach {
  licenseNumber: string;
  firstName: string;
  lastName: string;
  disciplines?: Discipline[];
  athletes?: Athlete[]|null;
}
