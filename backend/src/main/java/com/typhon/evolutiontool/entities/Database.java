package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Database database = (Database) o;
        return Objects.equals(type, database.type) &&
                Objects.equals(name, database.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }
}