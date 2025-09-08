'use strict';

// Helper to build athlete IDs with format A001, A002...
function athleteId(num) {
    return `A${num.toString().padStart(3, '0')}`;
}

// Helper to generate random integers between min and max (inclusive)
function rnd(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

module.exports = {
    // Executed at the beginning of each scenario iteration
    seedRandoms: function (context, events, done) {
        // Athlete IDs: A001 - A005 (String expected in the backend)
        const athleteNum = rnd(1, 5);
        context.vars.athleteId = athleteId(athleteNum);

        // Coach IDs: 1001 - 1005 (not used in rest.yml yet)
        context.vars.coachId = rnd(1001, 1005);

        // Event IDs: adjust the range to match your dataset
        context.vars.eventId = rnd(1, 6);

        // Page numbers for paginated endpoints
        context.vars.page = rnd(0, 20);

        return done();
    }
};