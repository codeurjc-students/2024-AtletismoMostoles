export interface PdfHistory {
  requestId: string;
  atletaId: number;
  eventoId?: number;
  timestampGenerado: string;
  urlBlob?: string;
  estado: string;
}
