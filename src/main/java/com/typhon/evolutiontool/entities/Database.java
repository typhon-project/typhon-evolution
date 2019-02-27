package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.data.annotation.Id;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RelationalDB.class, name = "relationaldb"),
        @JsonSubTypes.Type(value = DocumentDB.class, name = "documentdb")
})
public abstract class Database {

    private Database type;

    @Id
    private String name;

    public Database getType() {
        return type;
    }

    public void setType(Database type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}