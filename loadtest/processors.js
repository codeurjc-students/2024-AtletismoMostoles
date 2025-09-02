'use strict';

// Simple random int helper
function rnd(min, max) { return Math.floor(Math.random() * (max - min + 1)) + min; }

module.exports = {
    // Called at the start of each scenario iteration
    seedRandoms: function (context, events, done) {
        // Tune ranges to match your dataset. If IDs don't exist, endpoints still return 200 (empty page).
        context.vars.athleteId = String(rnd(1, 5000));  // controller expects String
        context.vars.eventId = rnd(1, 200);             // controller expects Long
        context.vars.page = rnd(0, 50);
        return done();
    }
};
