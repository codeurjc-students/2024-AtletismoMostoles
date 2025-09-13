'use strict';

function beforeRequest(req, context, ee, next) {
    if (context.vars && context.vars.jwt) {
        req.headers = req.headers || {};
        req.headers.Authorization = `Bearer ${context.vars.jwt}`;
    }
    return next();
}

function afterResponse(req, res, context, ee, next) {
    try {
        if (req && typeof req.url === 'string' && req.url.includes('/api/auth/login')) {
            const body = JSON.parse(res.body || '{}');

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
                const keys = Object.keys(body || {});
                console.log('[login] No token key found. Body keys:', keys);
            }
        }
    } catch (e) {
       // console.log('afterResponse error:', e);
    }
    return next();
}

module.exports = { beforeRequest, afterResponse };
