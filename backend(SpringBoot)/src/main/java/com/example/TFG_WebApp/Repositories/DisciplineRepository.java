package com.example.TFG_WebApp.Repositories;


import com.example.TFG_WebApp.Models.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, Long>, JpaSpecificationExecutor<Discipline> {
}