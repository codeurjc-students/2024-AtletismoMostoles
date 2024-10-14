package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Models.Discipline;
import com.example.TFG_WebApp.Repositories.DisciplineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisciplineService {

    @Autowired
    private DisciplineRepository disciplineRepository;

    public List<Discipline> getAllDisciplines(){
        return disciplineRepository.findAll();
    }

    public Discipline getDisciplineById(String id){
        return disciplineRepository.getReferenceById(id);
    }
}
