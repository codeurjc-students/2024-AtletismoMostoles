package com.example.TFG_WebApp.Repositories;

import com.example.TFG_WebApp.Models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Collection<Event> findByDateAfterOrderByDate(LocalDate date);

    Collection<Event> findByOrganizersOrderByDate(Boolean organizer);

    @Query(value = "SELECT * FROM event WHERE MONTH(date) = :month AND YEAR(date) = :year", nativeQuery = true)
    List<Event> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
