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
  private eventSubscription?: () => void;

  constructor() {
    this.stompClient = new Client({
      brokerURL: undefined, // usamos SockJS
      webSocketFactory: () => new SockJS('/ws'),
      reconnectDelay: 5000,
      debug: (msg: string) => console.log('[WebSocket] ' + msg),
      onConnect: () => {
        this.connected = true;
        console.log('[WebSocket] Conectado correctamente');

        this.reSubscribePDF();
        this.reSubscribeEvents();
      },
      onStompError: (frame) => {
        console.error('[WebSocket] Error STOMP:', frame);
      }
    });

    this.stompClient.activate();
  }

  listenConfirmationPdf(licenseNumber: string, callback: (url: string) => void): void {
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

  listenEvents(callback: (event: any) => void): void {
    const topic = `/topic/eventos`;

    const subscribe = () => {
      const sub = this.stompClient.subscribe(topic, (message: IMessage) => {
        const event = JSON.parse(message.body);
        console.log('[WebSocket] Evento recibido:', event);
        callback(event);
      });

      this.eventSubscription = () => sub.unsubscribe();
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

  private reSubscribePDF(): void {
    const oldSubs = this.pdfSubscriptions;
    this.pdfSubscriptions = new Map();
    oldSubs.forEach((_unsub, licenseNumber) => {
      this.listenConfirmationPdf(licenseNumber, () => {});
    });
  }

  private reSubscribeEvents(): void {
    if (this.eventSubscription) {
      this.listenEvents(() => {});
    }
  }

  disconnect(): void {
    this.pdfSubscriptions.forEach(unsub => unsub());
    this.pdfSubscriptions.clear();

    if (this.eventSubscription) {
      this.eventSubscription();
      this.eventSubscription = undefined;
    }

    if (this.stompClient && this.connected) {
      this.stompClient.deactivate();
      this.connected = false;
      console.log('[WebSocket] Desconectado');
    }
  }
}
