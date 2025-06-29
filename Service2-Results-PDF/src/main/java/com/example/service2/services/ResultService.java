package com.example.service2.services;

import com.example.service2.entities.Result;

import java.util.List;

public interface ResultService {

    List<Result> findByAtletaId(Long atletaId);

    List<Result> findByEventoId(Long eventoId);

    Result save(Result result);

    Result findById(Long id);

    List<Result> findByEventoIdAndDisciplinaId(Long eventoId, Long disciplinaId);

    Result update(Long id, Result result);

    void delete(Long id);

}
