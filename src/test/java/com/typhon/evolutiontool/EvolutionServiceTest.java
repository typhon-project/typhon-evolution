package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import com.typhon.evolutiontool.services.TyphonDLConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EvolutionServiceTest {

    @Mock
    TyphonDLConnector typhonDLConnection;

    @InjectMocks
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

    @Test
    public void testVerifyTyphonDLStructureForCreateEntity() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid.json"));
        evolutionService.addEntity(smo);
        verify(typhonDLConnection, times(1)).isRunning(smo.getInputParameter().get("databasetype").toString(),smo.getInputParameter().get("databasename").toString());
    }

}
