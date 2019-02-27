package com.typhon.evolutiontool.entities;

import org.springframework.data.annotation.Id;

public class TyphonDatabase {

    private String databaseType;

    private String host;

    @Id
    private String databasename;
}
