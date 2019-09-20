package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum EvolutionOperator {

    @JsonAlias("add")
    ADD,
    @JsonAlias("remove")
    REMOVE,
    @JsonAlias("rename")
    RENAME,
    @JsonAlias("split")
    SPLITVERTICAL,
    @JsonAlias("merge")
    MERGE,
    @JsonAlias("migrate")
    MIGRATE,
    @JsonAlias("splithorizontal")
    SPLITHORIZONTAL,
    ENABLECONTAINMENT,
    DISABLECONTAINMENT,
    ENABLEOPPOSITE,
    DISABLEOPPOSITE;
}
