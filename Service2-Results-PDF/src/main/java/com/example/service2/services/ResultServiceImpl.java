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

    @Override
    public Result findById(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resultado no encontrado con ID: " + id));
    }

    @Override
    public List<Result> findByEventoIdAndDisciplinaId(Long eventoId, Long disciplinaId) {
        return resultRepository.findByEventoIdAndDisciplinaId(eventoId, disciplinaId);
    }

    @Override
    public Result update(Long id, Result updatedResult) {
        Result existing = resultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resultado no encontrado con ID: " + id));

        existing.setAtletaId(updatedResult.getAtletaId());
        existing.setEventoId(updatedResult.getEventoId());
        existing.setDisciplinaId(updatedResult.getDisciplinaId());
        existing.setMarca(updatedResult.getMarca());
        existing.setFecha(updatedResult.getFecha());

        return resultRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        resultRepository.deleteById(id);
    }

}
