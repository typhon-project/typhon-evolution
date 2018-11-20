package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.TyphonMLObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class SMOTests {

    private ObjectMapper mapper;
    private SMO smo;

    @Before
    public void setUp(){
        mapper = new ObjectMapper();
        smo = new SMO(TyphonMLObject.ENTITY, EvolutionOperator.ADD);
        try {
            smo.setInputParameter(mapper.readTree("{\"entity\":\"Professor\",\"attributes\":{\"name\":\"string\",\"hireDate\":\"date\"}, \"databasetype\":\"relationaldb\"" +
                    ",\"databasemappingname\":\"Professor\",\"id\":\"name\"}"));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCreateSMO() {
        assertEquals(smo.getTyphonObject(), TyphonMLObject.ENTITY);
        assertEquals(smo.getEvolutionOperator(), EvolutionOperator.ADD);
    }

    @Test
    public void testInputParameterEntityString(){
        JsonNode entitynamenode, attributesnode, inputParameter;
        inputParameter = smo.getInputParameter();
        entitynamenode = inputParameter.get("entity");
        assertEquals("Professor", entitynamenode.textValue());

        attributesnode = inputParameter.get("attributes");
        assertEquals("string",attributesnode.get("name").textValue());
    }

    @Test
    public void testInputParameterCastToEntityObject(){
        Entity entity;
        try {
            entity = mapper.treeToValue(smo.getInputParameter(), Entity.class);
            assertEquals("Professor",entity.getEntityName());
            assertEquals("string", entity.getAttributes().get("name").textValue());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

}
