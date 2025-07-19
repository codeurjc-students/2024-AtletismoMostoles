declare module 'sockjs-client/dist/sockjs.js' {
  const SockJS: {
    new (url: string, _reserved?: any, options?: any): WebSocket;
  };
  export default SockJS;
}
