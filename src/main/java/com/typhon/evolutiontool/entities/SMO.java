package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.JsonNode;

@JsonRootName("smo")
public class SMO {

    private TyphonMLObject typhonObject;
    private EvolutionOperator evolutionOperator;
    @JsonProperty("input")
    private JsonNode inputParameter;
//    private InputParameter inputParameter;

    public SMO(TyphonMLObject typhonObject, EvolutionOperator evolutionOperator) {
        this.typhonObject=typhonObject;
        this.evolutionOperator=evolutionOperator;
    }

    public SMO() {
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

    @Override
    public String toString() {
        return "SMO{" +
                "typhonObject=" + typhonObject +
                ", evolutionOperator=" + evolutionOperator +
                ", inputParameter=" + inputParameter +
                '}';
    }
}
