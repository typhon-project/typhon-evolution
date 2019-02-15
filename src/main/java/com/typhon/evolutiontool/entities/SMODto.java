package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SMODto {
    @JsonProperty("typhonobject")
    private TyphonMLObject typhonObject;
    @JsonProperty("operator")
    private EvolutionOperator evolutionOperator;
    @JsonProperty("parameters")
    private Map<String,Object> inputParameter;

    public SMODto(){}

    public SMODto(TyphonMLObject typhonObject, EvolutionOperator evolutionOperator) {
        this.typhonObject = typhonObject;
        this.evolutionOperator = evolutionOperator;
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

    public Map<String, Object> getInputParameter() {
        return inputParameter;
    }

    public void setInputParameter(Map<String,Object> inputParameter) {
        this.inputParameter = inputParameter;
    }
}
