package com.example.service1.Repositories;

import com.example.service1.Entities.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, Long>, JpaSpecificationExecutor<Discipline> {
}