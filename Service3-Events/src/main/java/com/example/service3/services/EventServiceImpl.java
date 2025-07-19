package com.example.service3.services;

import com.example.service3.dto.EventNotification;
import com.example.service3.entities.Event;
import com.example.service3.messaging.EventNotificationSender;
import com.example.service3.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final EventNotificationSender notificationSender;

    public EventServiceImpl(EventRepository eventRepository,
                            EventNotificationSender notificationSender) {
        this.eventRepository = eventRepository;
        this.notificationSender = notificationSender;
    }

    @Override
    public List<Event> findAllOrdered() {
        return eventRepository.findAllByOrderByDateAsc();
    }

    @Override
    public List<Event> findEventsAfter(LocalDateTime date) {
        return eventRepository.findByCreationTimeAfter(date);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public Event save(Event event) {
        Event savedEvent = eventRepository.save(event);

        EventNotification notification = new EventNotification();
        notification.setEventoId(savedEvent.getId());
        notification.setName(savedEvent.getName());
        notification.setDate(savedEvent.getDate().toString());
        notification.setMapLink(savedEvent.getMapLink());
        notification.setImageLink(savedEvent.getImageLink());
        notification.setOrganizedByClub(savedEvent.isOrganizedByClub());

        // Enviar por RabbitMQ y WebSocket
        notificationSender.sendNotification(notification);

        return savedEvent;
    }

    @Override
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }
}
