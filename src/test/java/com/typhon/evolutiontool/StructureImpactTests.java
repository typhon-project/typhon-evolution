package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.services.EvolutionToolFacade;
import com.typhon.evolutiontool.services.EvolutionToolFacadeImpl;
import com.typhon.evolutiontool.services.StructureChange;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.springframework.ui.ModelMap;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class StructureImpactTests {

    private EvolutionToolFacade evolutionToolFacade = new EvolutionToolFacadeImpl();
    private ObjectMapper mapper;
    private SMO smo;
    private final String ADD_ENTITY_JSON = "{"+
            "\"smo\":{"+
            "\"typhonObject\":\"Entity\","+
            "\"evolutionOperator\":\"Add\","+
            "\"input\":{"+
            "\"entity\":\"Professor\","+
            "\"attributes\":{"+
            "\"name\":\"string\","+
            "\"hireDate\":\"date\""+
            "},"+
            "\"databasetype\":\"relationaldb\","+
            "\"databasemappingname\":\"Professor\","+
            "\"id\":\"name\""+
            "}"
            +"}"
            +"}";

    @Before
    public void setUp() throws IOException {
        ModelMapper modelMapper = new ModelMapper();
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        SMODto smoDto = mapper.readValue(ADD_ENTITY_JSON, SMODto.class);
        smo = modelMapper.map(smoDto, SMO.class);
    }

    @Test
    public void testCalculateImpactStructure() {
        //Initialisation expected value
        Entity entity = new Entity();
        entity.setEntityName("Professor");
        String tqlddl = "CREATE ENTITY "+ entity.getEntityName();
        StructureChange structureChange;
        //Get actual value
        structureChange = evolutionToolFacade.createStructureChanges(smo);

        //verify
        assertEquals(tqlddl,structureChange.getTqlQuery());
        assertEquals(smo, structureChange.getSmo());
    }
}
