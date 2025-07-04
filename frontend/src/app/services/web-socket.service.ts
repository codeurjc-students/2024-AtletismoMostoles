import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs.js';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Client;
  private connected = false;

  constructor() {
    this.stompClient = new Client({
      brokerURL: undefined, // usamos SockJS
      webSocketFactory: () => new SockJS('https://localhost:443/ws'),
      reconnectDelay: 5000,
      debug: (msg: string) => console.log('[WebSocket] ' + msg),
      onConnect: () => {
        this.connected = true;
        console.log('[WebSocket] Conectado correctamente');
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
      this.stompClient.subscribe(topic, (message: IMessage) => {
        const url = message.body;
        console.log('[WebSocket] Mensaje recibido:', url);
        callback(url);
      });
    };

    if (this.connected) {
      subscribe();
    } else {
      this.stompClient.onConnect = () => {
        this.connected = true;
        console.log('[WebSocket] Conectado tras reconexión');
        subscribe();
      };
    }
  }
}
