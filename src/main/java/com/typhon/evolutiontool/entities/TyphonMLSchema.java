package com.typhon.evolutiontool.entities;

public class TyphonMLSchema {

    String id;

    public TyphonMLSchema(String typhonMLVersion) {
        this.id = typhonMLVersion;
    }

    public String getId() {
        return id;
    }
}
