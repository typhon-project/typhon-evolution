package com.typhon.evolutiontool.entities;

import typhonml.ChangeOperator;
import typhonml.RemoveEntity;
import typhonml.RenameEntity;

import java.util.List;
import java.util.Map;

public class SMOAdapter implements SMO {

    private ChangeOperator changeOperator;

    public SMOAdapter(ChangeOperator changeOperator) {
        this.changeOperator = changeOperator;
    }

    @Override
    public TyphonMLObject getTyphonObject() {
        if(changeOperator instanceof RemoveEntity || changeOperator instanceof RenameEntity)
            return TyphonMLObject.ENTITY;
        return null;
    }

    @Override
    public void setTyphonObject(TyphonMLObject typhonObject) {

    }

    @Override
    public EvolutionOperator getEvolutionOperator() {
        if(changeOperator instanceof RemoveEntity)
            return EvolutionOperator.REMOVE;
        if(changeOperator instanceof RenameEntity)
            return EvolutionOperator.RENAME;

        return null;
    }

    @Override
    public void setEvolutionOperator(EvolutionOperator evolutionOperator) {

    }

    @Override
    public Map<String, Object> getInputParameter() {
        return null;
    }

    @Override
    public void setInputParameter(Map<String, Object> inputParameter) {

    }

    @Override
    public boolean inputParametersContainsExpected(List<String> expectedInputParams) {
        return false;
    }

    @Override
    public <T> T getPOJOFromInputParameter(String key, Class<T> pojoclass) {
        return null;
    }
}
