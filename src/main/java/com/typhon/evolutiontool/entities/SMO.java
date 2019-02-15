package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@JsonRootName("smo")
public class SMO {

    @JsonProperty("typhonobject")
    private TyphonMLObject typhonObject;
    @JsonProperty("operator")
    private EvolutionOperator evolutionOperator;
    @JsonProperty("parameters")
    private Map<String,Object> inputParameter;

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

    public Map<String,Object> getInputParameter() {
        return inputParameter;
    }

    public void setInputParameter(Map<String,Object> inputParameter) {
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

    public boolean verifyInputParameters(List<String> expectedInputParams) {
        for (String expected :
                expectedInputParams) {
            if (!this.inputParameter.containsKey(expected))
                return false;
        }
        return true;
    }
}
