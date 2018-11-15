package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TyphonMLObject {

    @JsonProperty("Entity")
    ENTITY,
    @JsonProperty("Attribute")
    ATTRIBUTE,
    @JsonProperty("Relation")
    RELATION,

}
