'use strict';

// STOMP frames builder (very simple)
const NULL = '\x00';
function frame(command, headers = {}, body = '') {
    const lines = [command];
    for (const [k, v] of Object.entries(headers)) {
        lines.push(`${k}:${v}`);
    }
    lines.push('');          // blank line before body
    const payload = lines.join('\n') + (body || '') + NULL;
    return payload;
}

// Helpers to send/await frames with Artillery WS engine
function sendWs(context, payload) {
    return new Promise((resolve, reject) => {
        context.ws.send(payload, (err) => (err ? reject(err) : resolve()));
    });
}

module.exports = {
    stompConnect: async function (context, events, done) {
        try {
            // If Spring defaults: accept-version 1.2, host arbitrary
            const payload = frame('CONNECT', {
                'accept-version': '1.2',
                'host': 'localhost'
            });
            await sendWs(context, payload);
            // You could add a small wait to receive CONNECTED frame; artillery doesn't expose easy WS recv hooks per step,
            // but keeping a think time is usually enough for load tests.
            return done();
        } catch (e) { return done(e); }
    },

    stompSubscribeResults: async function (context, events, done) {
        try {
            // Subscribe to your topic
            // Spring broker dest is /topic/... (you configured enableSimpleBroker("/topic"))
            const subId = `sub-${Math.floor(Math.random()*1e6)}`;
            context.vars.subId = subId;
            const payload = frame('SUBSCRIBE', {
                id: subId,
                destination: '/topic/results'   // <-- ajusta si tu app publica en otro topic
            });
            await sendWs(context, payload);
            return done();
        } catch (e) { return done(e); }
    },

    // OPTIONAL: send an application message (only if your app listens on /app/someMapping)
    stompSendApp: async function (context, events, done) {
        try {
            const body = JSON.stringify({ ping: true, ts: Date.now() });
            const payload = frame('SEND', {
                destination: '/app/ping'        // <-- ajusta a tu mapping de @MessageMapping si existe
            }, body);
            await sendWs(context, payload);
            return done();
        } catch (e) { return done(e); }
    },

    stompDisconnect: async function (context, events, done) {
        try {
            const payload = frame('DISCONNECT', {});
            await sendWs(context, payload);
            return done();
        } catch (e) { return done(e); }
    }
};
