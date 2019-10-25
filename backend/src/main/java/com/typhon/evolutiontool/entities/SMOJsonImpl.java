package com.typhon.evolutiontool.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class SMOJsonImpl implements SMO {

    Logger logger = LoggerFactory.getLogger(SMOJsonImpl.class);

    private TyphonMLObject typhonObject;
    private EvolutionOperator evolutionOperator;
    private Map<String, Object> inputParameter;

    public SMOJsonImpl(TyphonMLObject typhonObject, EvolutionOperator evolutionOperator) {
        this.typhonObject = typhonObject;
        this.evolutionOperator = evolutionOperator;
    }

    public SMOJsonImpl() {
    }


    @Override
    public TyphonMLObject getTyphonObject() {
        return typhonObject;
    }


    @Override
    public EvolutionOperator getEvolutionOperator() {
        return evolutionOperator;
    }

    @Override
    public Map<String, Object> getInputParameter() {
        return inputParameter;
    }

    @Override
    public String toString() {
        return "SMO{" +
                "typhonObject=" + typhonObject +
                ", evolutionOperator=" + evolutionOperator +
                ", inputParameter=" + inputParameter +
                '}';
    }

    @Override
    public boolean inputParametersContainsExpected(List<String> expectedInputParams) {
        for (String expected :
                expectedInputParams) {
            if (!this.inputParameter.containsKey(expected))
                return false;
        }
        return true;
    }

}
