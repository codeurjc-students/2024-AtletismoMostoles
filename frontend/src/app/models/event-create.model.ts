export interface EventCreate {
  name: string;
  imageLink: string;
  mapLink: string;
  date: string;
  organizedByClub: boolean;
  disciplineIds: number[];
}
