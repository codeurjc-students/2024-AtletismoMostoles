import { Coach } from './coach.model';
import { Discipline } from './discipline.model';
import { Results } from './results.model';

export interface Athlete {
  licenseNumber: string;
  firstName: string;
  lastName: string;
  birthDate: string;
  coach?: Coach|null;
  disciplines?: Discipline[]|null;
  results?: Results[];
}
