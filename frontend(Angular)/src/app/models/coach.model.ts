import {Athlete} from './athlete.model';

export interface Coach{
  licenseNumber: string;
  firstName: string;
  lastName: string;
  athletes: Athlete[];
};
