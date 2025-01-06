
export interface Discipline {
  id: number;
  name: string;
  description: string;
  image: string;
  coaches: { licenseNumber: string; firstName: string; lastName: string }[];
};
