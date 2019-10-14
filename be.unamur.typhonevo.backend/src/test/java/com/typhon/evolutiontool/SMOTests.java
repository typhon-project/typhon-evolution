package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.*;

public class SMOTests {

    private String CREATE_ENTITY_FILE_PATH = "src/main/resources/test/CreateEntitySmoValid.json";
    private ObjectMapper mapper;
    private SMO smo;
    private SMODto smoDto;

    @Before
    public void setUp() throws IOException {
        File smoJsonFile = new File(CREATE_ENTITY_FILE_PATH);
        mapper = new ObjectMapper();
        smoDto = mapper.readerFor(SMODto.class).readValue(smoJsonFile);
    }

    @Test
    public void testCreateSMODto() {
        assertNotNull(smoDto);
        assertEquals(TyphonMLObject.ENTITY, smoDto.getTyphonObject());
        assertEquals(EvolutionOperator.ADD, smoDto.getEvolutionOperator());
        assertNotNull(smoDto.getInputParameter());
    }

    @Test
    public void testInputParameterAsMap() {
        assertNotNull(smoDto.getInputParameter());
        assertNotNull(smoDto.getInputParameter().get("targetmodel"));
    }

    @Test
    public void testVerifyInputParameters() throws IOException {
        List<String> expectedInputParams = Arrays.asList("entity", "targetmodel");
        smo = mapper.readerFor(SMOJsonImpl.class).readValue(new File(CREATE_ENTITY_FILE_PATH));
        assertTrue(smo.inputParametersContainsExpected(expectedInputParams));
        expectedInputParams = Collections.singletonList("entity");
        assertTrue(smo.inputParametersContainsExpected(expectedInputParams));
        assertFalse(smo.inputParametersContainsExpected(Collections.singletonList("notin")));
    }

    @Test
    public void testGetParameter() throws IOException {
        smo = mapper.readerFor(SMOJsonImpl.class).readValue(new File(CREATE_ENTITY_FILE_PATH));
        assertEquals("TyphonML_V2", smo.getInputParameter().get("targetmodel"));
    }


    @Test
    public void testCastInputParameterToPOJO() throws IOException {
        EntityDO expectedEntity, inputEntity;
        expectedEntity = new EntityDOJsonImpl("client");
        expectedEntity.addAttribute("name", "string");
        expectedEntity.addAttribute("entrydate", "date");
        smo = mapper.readerFor(SMOJsonImpl.class).readValue(new File(CREATE_ENTITY_FILE_PATH));

        inputEntity = smo.getPOJOFromInputParameter("entity", EntityDOJsonImpl.class);
        assertEquals(expectedEntity, inputEntity);

    }
}
