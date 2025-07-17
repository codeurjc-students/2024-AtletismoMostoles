package com.example.service2.dto;

public class EventDto {
    private Long id;
    private String name;

    public EventDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
