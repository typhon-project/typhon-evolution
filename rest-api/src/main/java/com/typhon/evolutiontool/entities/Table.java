package com.typhon.evolutiontool.entities;

import org.springframework.data.annotation.Id;

public class Table {

    @Id
    private String name;
    private String entity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
