package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.*;

public class EvolutionServiceTest {

    EvolutionServiceImpl evolutionService= new EvolutionServiceImpl();
    private ObjectMapper mapper = new ObjectMapper();
    private SMO smo;


    @Test
    public void testCreateEntityParameter() throws IOException {
        try {
            smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid.json"));
            assertTrue(evolutionService.addEntity(smo).equals("entity created"));
            smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoIncompleteParam.json"));
            evolutionService.addEntity(smo);
            fail();
        } catch (InputParameterException exception) {
            assertTrue(exception.getMessage().contains("Missing parameter"));
        }
    }

}
