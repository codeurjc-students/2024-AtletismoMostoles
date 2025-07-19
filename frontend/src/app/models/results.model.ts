
export interface Results {
  id?: number;
  atletaId: string;
  eventoId: number;
  disciplinaId: number;
  valor: string;
  athlete?: {
    firstName: string;
    lastName: string;
  };
  discipline?: {
    name: string;
  };
}

