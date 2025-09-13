'use strict';

// Añade Authorization: Bearer <jwt> a todas las peticiones si hay token
function beforeRequest(req, context, ee, next) {
    if (context.vars && context.vars.jwt) {
        req.headers = req.headers || {};
        req.headers.Authorization = `Bearer ${context.vars.jwt}`;
    }
    return next();
}

// Captura robusta del token tras el login (soporta varias claves/estructuras)
function afterResponse(req, res, context, ee, next) {
    try {
        // Cuando sea la llamada de login
        if (req && typeof req.url === 'string' && req.url.includes('/api/auth/login')) {
            const body = JSON.parse(res.body || '{}');

            // Candidatas típicas para el JWT
            const candidates = [
                body.token,
                body.jwt,
                body.accessToken,
                body.access_token,
                body.id_token,
                body?.data?.token,
                body?.data?.jwt,
                body?.data?.accessToken
            ].filter(Boolean);

            if (candidates.length > 0) {
                context.vars.jwt = candidates[0];
            } else {
                // Ayuda de depuración: muestra las claves disponibles
                // (no rompe el test; sólo informa en consola)
                const keys = Object.keys(body || {});
                console.log('[login] No token key found. Body keys:', keys);
            }
        }
    } catch (e) {
        // Silencioso para no romper el test
        // console.log('afterResponse error:', e);
    }
    return next();
}

module.exports = { beforeRequest, afterResponse };
