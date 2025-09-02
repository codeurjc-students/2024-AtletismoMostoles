
export interface Results {
  id?: number;
  athleteId: string;
  eventId: number;
  disciplineId: number;
  value: string;
  athlete?: {
    firstName: string;
    lastName: string;
  };
  discipline?: {
    name: string;
  };
}

