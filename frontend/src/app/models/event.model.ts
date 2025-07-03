import { Discipline } from "./discipline.model";
import { Results } from "./results.model";

export interface Event {
  id: number;
  name: string;
  date: string;
  mapLink?: string;
  organizedByClub: boolean;
  imageLink?: string;
  disciplines?: Discipline[];
  results?: Results[];
}
