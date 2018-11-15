package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.JsonNode;

@JsonRootName("smo")
public class SMODto {
    private TyphonMLObject typhonObject;
    private EvolutionOperator evolutionOperator;
    @JsonProperty("input")
    private JsonNode inputParameter;

    public SMODto(){}

    public SMODto(TyphonMLObject typhonObject, EvolutionOperator evolutionOperator, JsonNode inputParameter) {
        this.typhonObject = typhonObject;
        this.evolutionOperator = evolutionOperator;
        this.inputParameter = inputParameter;
    }

    public TyphonMLObject getTyphonObject() {
        return typhonObject;
    }

    public void setTyphonObject(TyphonMLObject typhonObject) {
        this.typhonObject = typhonObject;
    }

    public EvolutionOperator getEvolutionOperator() {
        return evolutionOperator;
    }

    public void setEvolutionOperator(EvolutionOperator evolutionOperator) {
        this.evolutionOperator = evolutionOperator;
    }

    public JsonNode getInputParameter() {
        return inputParameter;
    }

    public void setInputParameter(JsonNode inputParameter) {
        this.inputParameter = inputParameter;
    }
}
