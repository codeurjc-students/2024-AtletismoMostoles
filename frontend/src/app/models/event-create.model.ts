export interface EventCreate {
  name: string;
  imageUrl: string;
  mapUrl: string;
  date: string;
  isOrganizedByClub: boolean;
  disciplines: { id: number }[];
}
