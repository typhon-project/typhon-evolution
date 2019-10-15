package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum TyphonMLObject {

    @JsonAlias("entity")
    ENTITY,
    @JsonAlias("attribute")
    ATTRIBUTE,
    @JsonAlias("relation")
    RELATION,

}
