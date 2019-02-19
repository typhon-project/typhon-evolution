package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.*;

public class SMOTests {

    private String createEntityFilePath = "src/main/resources/test/CreateEntitySmoValid.json";
    private File smoJsonFile;
    private ObjectMapper mapper;
    private SMO smo;
    private SMODto smoDto;

    @Before
    public void setUp() throws IOException {
        smoJsonFile = new File(createEntityFilePath);
        mapper = new ObjectMapper();
        smoDto = mapper.readerFor(SMODto.class).readValue(smoJsonFile);
    }

    @Test
    public void testCreateSMODto() {
        assertNotNull(smoDto);
        assertEquals(TyphonMLObject.ENTITY,smoDto.getTyphonObject());
        assertEquals(EvolutionOperator.ADD,smoDto.getEvolutionOperator());
        assertNotNull(smoDto.getInputParameter());
    }

    @Test
    public void testInputParameterAsMap(){
        assertTrue(smoDto.getInputParameter() instanceof Map);
        assertNotNull(smoDto.getInputParameter());
        assertNotNull(smoDto.getInputParameter().get("targetmodel"));
    }

    @Test
    public void testVerifyInputParameters() throws IOException {
        List<String> expectedInputParams = Arrays.asList("entity", "targetmodel");
        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid.json"));
        assertTrue(smo.inputParametersContainsExpected(expectedInputParams));
        expectedInputParams = Arrays.asList("entity");
        assertTrue(smo.inputParametersContainsExpected(expectedInputParams));
        assertFalse(smo.inputParametersContainsExpected(Arrays.asList("notin")));
    }

    @Test
    public void testGetParameter() throws IOException {
        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid.json"));
        assertEquals("TyphonML_V2",smo.getInputParameter().get("targetmodel"));
    }


    @Test
    public void testCastInputParameterToPOJO() throws IOException {
        Entity expectedEntity, inputEntity;
        Map<String, String> expectedAttributes = new HashMap<>();
        expectedAttributes.put("name", "string");
        expectedAttributes.put("hireDate", "date");
        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid.json"));
        expectedEntity = new Entity("Professor");
        expectedEntity.setAttributes(expectedAttributes);

        inputEntity = smo.getPOJOFromInputParameter("entity", Entity.class);
        assertEquals(expectedEntity,inputEntity);

    }
}
