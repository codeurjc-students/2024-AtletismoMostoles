package com.example.TFG_WebApp.Repositories;

import com.example.TFG_WebApp.Models.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultsRepository extends JpaRepository<Results, Long>, JpaSpecificationExecutor<Results> {
}
