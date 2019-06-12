package com.typhon.evolutiontool.entities;

import java.util.List;
import java.util.Map;

public interface SMO {
    TyphonMLObject getTyphonObject();

    void setTyphonObject(TyphonMLObject typhonObject);

    EvolutionOperator getEvolutionOperator();

    void setEvolutionOperator(EvolutionOperator evolutionOperator);

    Map<String,Object> getInputParameter();

    void setInputParameter(Map<String, Object> inputParameter);

    boolean inputParametersContainsExpected(List<String> expectedInputParams);

    <T> T getPOJOFromInputParameter(String key, Class<T> pojoclass);
}
