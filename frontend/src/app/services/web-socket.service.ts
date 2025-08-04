import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs.js';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Client;
  private connected = false;
  private pdfSubscriptions: Map<string, () => void> = new Map();
  private eventoSubscription?: () => void;

  constructor() {
    this.stompClient = new Client({
      brokerURL: undefined, // usamos SockJS
      webSocketFactory: () => new SockJS('/ws'),
      reconnectDelay: 5000,
      debug: (msg: string) => console.log('[WebSocket] ' + msg),
      onConnect: () => {
        this.connected = true;
        console.log('[WebSocket] Conectado correctamente');

        // Re-suscribir en caso de reconexión
        this.reSuscribirPdf();
        this.reSuscribirEventos();
      },
      onStompError: (frame) => {
        console.error('[WebSocket] Error STOMP:', frame);
      }
    });

    this.stompClient.activate();
  }

  /**
   * Se suscribe al tópico de PDF para un atleta específico.
   * @param licenseNumber ID del atleta
   * @param callback función que se ejecutará con la URL del PDF
   */
  escucharConfirmacionPdf(licenseNumber: string, callback: (url: string) => void): void {
    const topic = `/topic/pdf/${licenseNumber}`;

    const subscribe = () => {
      const sub = this.stompClient.subscribe(topic, (message: IMessage) => {
        const url = message.body;
        console.log('[WebSocket] PDF recibido:', url);
        callback(url);
      });

      this.pdfSubscriptions.set(licenseNumber, () => sub.unsubscribe());
    };

    if (this.connected) {
      subscribe();
    } else {
      const originalOnConnect = this.stompClient.onConnect;
      this.stompClient.onConnect = (frame) => {
        this.connected = true;
        if (originalOnConnect) originalOnConnect(frame);
        subscribe();
      };
    }
  }

  /**
   * Escucha notificaciones de nuevos eventos creados.
   * @param callback función que se ejecutará con la notificación recibida
   */
  escucharEventos(callback: (evento: any) => void): void {
    const topic = `/topic/eventos`;

    const subscribe = () => {
      const sub = this.stompClient.subscribe(topic, (message: IMessage) => {
        const evento = JSON.parse(message.body);
        console.log('[WebSocket] Evento recibido:', evento);
        callback(evento);
      });

      this.eventoSubscription = () => sub.unsubscribe();
    };

    if (this.connected) {
      subscribe();
    } else {
      const originalOnConnect = this.stompClient.onConnect;
      this.stompClient.onConnect = (frame) => {
        this.connected = true;
        if (originalOnConnect) originalOnConnect(frame);
        console.log('[WebSocket] Conectado tras reconexión (eventos)');
        subscribe();
      };
    }
  }

  /**
   * Re-suscribe a todos los topics de PDF en caso de reconexión.
   */
  private reSuscribirPdf(): void {
    const oldSubs = this.pdfSubscriptions;
    this.pdfSubscriptions = new Map();
    oldSubs.forEach((_unsub, licenseNumber) => {
      this.escucharConfirmacionPdf(licenseNumber, () => {});
    });
  }

  /**
   * Re-suscribe al topic de eventos en caso de reconexión.
   */
  private reSuscribirEventos(): void {
    if (this.eventoSubscription) {
      this.escucharEventos(() => {});
    }
  }

  /**
   * Limpia todas las suscripciones activas (PDFs y eventos).
   */
  desconectar(): void {
    this.pdfSubscriptions.forEach(unsub => unsub());
    this.pdfSubscriptions.clear();

    if (this.eventoSubscription) {
      this.eventoSubscription();
      this.eventoSubscription = undefined;
    }

    if (this.stompClient && this.connected) {
      this.stompClient.deactivate();
      this.connected = false;
      console.log('[WebSocket] Desconectado');
    }
  }
}
