package com.typhon.evolutiontool.entities;

import java.util.List;
import java.util.Map;

public interface SMO {

    TyphonMLObject getTyphonObject();

    EvolutionOperator getEvolutionOperator();

    Map<ChangeOperatorParameter, Object> getInputParameter();

    boolean inputParametersContainsExpected(List<ChangeOperatorParameter> expectedInputParams);

}
