package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionService;
import com.typhon.evolutiontool.services.EvolutionToolFacadeImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EvolutionToolFacadeTest {

    @Mock
    EvolutionService evolutionServiceMock;
    @InjectMocks
    EvolutionToolFacadeImpl evolutionToolFacade;

    private ObjectMapper mapper = new ObjectMapper();
    private SMO smo;

    @Test
    public void testExecuteAddEntitySMO() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/dummyCreateEntitySmo.json"));
        evolutionToolFacade.executeSMO(smo);
        verify(evolutionServiceMock).addEntity(smo);
    }

    @Test
    public void testExecuteRenameEntitySMO() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/dummyRenameEntitySmo.json"));
        evolutionToolFacade.executeSMO(smo);
        verify(evolutionServiceMock).renameEntity(smo);
    }


}
