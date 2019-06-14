package com.typhon.evolutiontool.entities;

import com.typhon.evolutiontool.utils.EntityDOFactory;
import typhonml.*;

import java.util.List;
import java.util.Map;


/**
 * This class converts ChangeOperatorof TyphonML package object to an intern SMO object used by the EvolutionService class.
 * Implementation is currently naive and non optimal as no attributes allow the detection of the specific operators. Downcasting is the current implementation.
 *
 */
public class SMOAdapter implements SMO {

    private ChangeOperator changeOperator;
    private Map<String, Object> inputParameter;

    public SMOAdapter(ChangeOperator changeOperator) {
        this.changeOperator = changeOperator;
    }

    @Override
    public TyphonMLObject getTyphonObject() {
        if(changeOperator instanceof RemoveEntity || changeOperator instanceof RenameEntity || changeOperator instanceof AddEntity)
            return TyphonMLObject.ENTITY;
        return null;
    }


    @Override
    public EvolutionOperator getEvolutionOperator() {
        if(changeOperator instanceof RemoveEntity)
            return EvolutionOperator.REMOVE;
        if(changeOperator instanceof RenameEntity)
            return EvolutionOperator.RENAME;
        if(changeOperator instanceof AddEntity)
            return EvolutionOperator.ADD;

        return null;
    }


    @Override
    public Map<String, Object> getInputParameter() {
        //TODO
        return null;
    }


    @Override
    public boolean inputParametersContainsExpected(List<String> expectedInputParams) {
        return false;
    }

    @Override
    public <T> T getPOJOFromInputParameter(String key, Class<T> pojoclass) {
        return null;
    }

    @Override
    public EntityDO getEntityDOFromInputParameter(String parameterkey) {
        //Because AddEntity Operator exends Enity in TyphonML meta model.
        if(changeOperator instanceof AddEntity)
            return EntityDOFactory.createEntityDOFromEntityML((AddEntity)changeOperator);
        return null;
    }
}
