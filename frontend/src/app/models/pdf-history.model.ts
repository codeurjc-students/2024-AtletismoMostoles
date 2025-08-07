export interface PdfHistory {
  requestId: string;
  athleteId: number;
  eventId?: number;
  timestampGenerated: string;
  urlBlob?: string;
  state: string;
}
