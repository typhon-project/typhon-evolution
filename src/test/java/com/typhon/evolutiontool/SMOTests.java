package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static junit.framework.TestCase.*;

public class SMOTests {

    private String createEntityFilePath = "src/main/resources/test/smoCreateEntity.json";
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


}
