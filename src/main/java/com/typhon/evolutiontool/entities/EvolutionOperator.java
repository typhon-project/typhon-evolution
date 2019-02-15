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
    SPLIT,
    @JsonAlias("merge")
    MERGE,
    @JsonAlias("migrate")
    MIGRATE;
}
