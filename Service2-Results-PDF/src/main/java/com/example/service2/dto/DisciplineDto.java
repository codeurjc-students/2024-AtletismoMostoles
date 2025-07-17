package com.example.service2.dto;

public class DisciplineDto {
    private Long id;
    private String name;

    public DisciplineDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
