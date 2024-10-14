package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Models.Event;
import com.example.TFG_WebApp.Repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Collection<Event> getNextEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findByDateAfter(today);
    }

    public Collection<Event> getEventeOrganized(Boolean organizer) {
        return eventRepository.findByOrganizers(organizer);
    }

}
