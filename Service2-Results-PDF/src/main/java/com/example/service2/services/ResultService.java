package com.example.service2.services;

import com.example.service2.entities.Result;

import java.util.List;

public interface ResultService {

    /**
     * Returns all results for a given athlete.
     *
     * @param atletaId the athlete's ID
     * @return list of results
     */
    List<Result> findByAtletaId(Long atletaId);

    /**
     * Returns all results for a given event.
     *
     * @param eventoId the event's ID
     * @return list of results
     */
    List<Result> findByEventoId(Long eventoId);

    /**
     * Saves a result in the system.
     *
     * @param result the result to save
     * @return the saved result
     */
    Result save(Result result);
}
