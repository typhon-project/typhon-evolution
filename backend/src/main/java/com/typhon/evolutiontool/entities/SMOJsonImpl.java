package com.typhon.evolutiontool.entities;

import java.util.List;
import java.util.Map;

public class SMOJsonImpl implements SMO {

    private TyphonMLObject typhonObject;
    private EvolutionOperator evolutionOperator;
    private Map<ChangeOperatorParameter, Object> inputParameter;

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
    public Map<ChangeOperatorParameter, Object> getInputParameter() {
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
    public boolean inputParametersContainsExpected(List<ChangeOperatorParameter> expectedInputParams) {
        for (ChangeOperatorParameter expected : expectedInputParams) {
            if (!this.getInputParameter().containsKey(expected))
                return false;
        }
        return true;
    }

}
