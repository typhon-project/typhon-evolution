package main.java.com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import main.java.com.typhon.evolutiontool.utils.MyKeyDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class SMOJsonImpl implements SMO {

    Logger logger = LoggerFactory.getLogger(SMOJsonImpl.class);

    @JsonProperty("typhonobject")
    private TyphonMLObject typhonObject;
    @JsonProperty("operator")
    private EvolutionOperator evolutionOperator;
    @JsonProperty("parameters")
    @JsonDeserialize(keyUsing = MyKeyDeserializer.class)
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

    @Override
    public <T> T getPOJOFromInputParameter(String key, Class<T> pojoclass) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(this.getInputParameter().get(key), pojoclass);
    }

    @Override
    public EntityDO getEntityDOFromInputParameter(String parameterkey) {
        if (this.getInputParameter().containsKey(parameterkey)) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(this.getInputParameter().get(parameterkey), EntityDOJsonImpl.class);
        }
        return null;
    }

    @Override
    public RelationDO getRelationDOFromInputParameter(String parameterkey) {
        if (this.getInputParameter().containsKey(parameterkey)) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(this.getInputParameter().get(parameterkey), RelationDOJsonImpl.class);

        }
        return null;
    }

    @Override
    public AttributeDO getAttributeDOFromInputParameter(String parameterkey) {
        return null;
    }

}
