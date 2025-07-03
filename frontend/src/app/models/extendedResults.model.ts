import { Results } from "./results.model";

export interface ExtendedResults extends Results {
  eventName: string;
  disciplineName: string;
}
