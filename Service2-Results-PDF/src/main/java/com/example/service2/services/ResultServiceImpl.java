package com.example.service2.services;

import com.example.service2.entities.Result;
import com.example.service2.repositories.ResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;

    public ResultServiceImpl(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public List<Result> findByAtletaId(Long atletaId) {
        return resultRepository.findByAtletaId(atletaId);
    }

    @Override
    public List<Result> findByEventoId(Long eventoId) {
        return resultRepository.findByEventoId(eventoId);
    }

    @Override
    public Result save(Result result) {
        return resultRepository.save(result);
    }
}
