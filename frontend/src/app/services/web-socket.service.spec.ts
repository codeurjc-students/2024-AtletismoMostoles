import { TestBed } from '@angular/core/testing';
import { WebSocketService } from './web-socket.service';
import { Client, IMessage } from '@stomp/stompjs';

describe('WebSocketService', () => {
  let service: WebSocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = new WebSocketService();
  });

  it('debería crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('debería suscribirse a PDF cuando está conectado', () => {
    const fakeMessage = {
      body: 'https://fake.pdf',
      ack: () => {},
      nack: () => {},
      headers: {},
      command: '',
      binaryBody: new Uint8Array(),
      isBinaryBody: false
    } as IMessage;

    const spyCallback = jasmine.createSpy();

    service['connected'] = true;

    spyOn(service['stompClient'], 'subscribe').and.callFake((_topic, callback) => {
      callback(fakeMessage);
      return { unsubscribe: () => {} } as any;
    });

    service.escucharConfirmacionPdf('123', spyCallback);

    expect(spyCallback).toHaveBeenCalledWith('https://fake.pdf');
  });


  it('debería suscribirse a eventos cuando está conectado', () => {
    const eventoMock = { id: 99, name: 'evento' };
    const fakeMessage: IMessage = {
      body: JSON.stringify(eventoMock),
      ack: () => {},
      nack: () => {},
      command: '',
      headers: {},
      isBinaryBody: false,
      binaryBody: new Uint8Array()
    };

    const spyCallback = jasmine.createSpy();

    service['connected'] = true;

    spyOn(service['stompClient'], 'subscribe').and.callFake((_topic, callback) => {
      callback(fakeMessage);
      return { unsubscribe: () => {} } as any;
    });

    service.escucharEventos(spyCallback);

    expect(spyCallback).toHaveBeenCalledWith(eventoMock);
  });

  it('debería limpiar todas las suscripciones al desconectar', () => {
    const pdfUnsub = jasmine.createSpy('pdfUnsub');
    const eventUnsub = jasmine.createSpy('eventUnsub');

    service['pdfSubscriptions'].set('123', pdfUnsub);
    service['eventoSubscription'] = eventUnsub;
    service['connected'] = true;

    spyOn(service['stompClient'], 'deactivate');

    service.desconectar();

    expect(pdfUnsub).toHaveBeenCalled();
    expect(eventUnsub).toHaveBeenCalled();
    expect(service['stompClient'].deactivate).toHaveBeenCalled();
    expect(service['connected']).toBeFalse();
  });
});
