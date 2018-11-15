package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EvolutionOperator {

    @JsonProperty("Add")
    ADD,
    REMOVE,
    RENAME,
    SPLIT,
    MERGE,
    MIGRATE;
}
