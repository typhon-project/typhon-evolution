package main.java.com.typhon.evolutiontool.entities;

import java.util.List;
import java.util.Map;

public interface SMO {
	
    TyphonMLObject getTyphonObject();

    EvolutionOperator getEvolutionOperator();

    Map<String, Object> getInputParameter();

    boolean inputParametersContainsExpected(List<String> expectedInputParams);
    
}
