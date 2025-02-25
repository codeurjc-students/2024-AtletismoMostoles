package com.example.TFG_WebApp.Repositories;

import com.example.TFG_WebApp.Models.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachRepository extends JpaRepository<Coach, String>, JpaSpecificationExecutor<Coach> {
}

