import { Discipline } from './discipline.model';

export interface Equipment {
  id: number;
  name: string;
  description: string;
  imageLink?: string;
  disciplines?: Discipline[];
}
