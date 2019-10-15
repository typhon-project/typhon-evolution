package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.TreeMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SMODto {
    @JsonProperty(ParametersKeyString.TYPHONOBJECT)
    private TyphonMLObject typhonObject;
    @JsonProperty(ParametersKeyString.EVOLUTIONOPERATOR)
    private EvolutionOperator evolutionOperator;
    @JsonProperty(ParametersKeyString.PARAMETERS)
    private Map<String,Object> inputParameter = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);

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
