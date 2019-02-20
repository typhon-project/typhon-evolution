package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import com.typhon.evolutiontool.services.TyphonDLConnector;
import com.typhon.evolutiontool.services.TyphonInterface;
import com.typhon.evolutiontool.services.TyphonMLInterface;
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
    @Mock
    TyphonInterface typhonInterface;
    @Mock
    TyphonMLInterface typhonMLInterface;
    @InjectMocks
    EvolutionServiceImpl evolutionService= new EvolutionServiceImpl();
    private ObjectMapper mapper = new ObjectMapper();
    private SMO smo;


    @Test
    public void testCreateEntity() throws IOException {
        try {
            smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid.json"));
            evolutionService.addEntity(smo);
            verify(typhonDLConnection).isDatabaseRunning(anyString(), anyString());
            verify(typhonInterface).createEntity(any(Entity.class));
            verify(typhonMLInterface).setNewTyphonMLModel(anyString());
//            assertTrue(evolutionService.addEntity(smo).equals("entity created"));
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
        //Database is running case
        when(typhonDLConnection.isDatabaseRunning(smo.getInputParameter().get("databasetype").toString(), smo.getInputParameter().get("databasename").toString())).thenReturn(true);
        evolutionService.addEntity(smo);
        // Verify that isDatabaseRunning is called. And CreateDatabase is not called.
        verify(typhonDLConnection, times(1)).isDatabaseRunning(smo.getInputParameter().get("databasetype").toString(),smo.getInputParameter().get("databasename").toString());
        verify(typhonDLConnection, times(0)).createDatabase(smo.getInputParameter().get("databasetype").toString(),smo.getInputParameter().get("databasename").toString());
        //Database is not running case
        when(typhonDLConnection.isDatabaseRunning(smo.getInputParameter().get("databasetype").toString(), smo.getInputParameter().get("databasename").toString())).thenReturn(false);
        evolutionService.addEntity(smo);
        //Verify that createDatabase method is called.
        verify(typhonDLConnection, times(1)).createDatabase(smo.getInputParameter().get("databasetype").toString(),smo.getInputParameter().get("databasename").toString());
    }



    /*
    ** RENAME ENTITY
     */

    @Test
    public void testRenameEntity() throws IOException {
        try {
            smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/RenameEntitySmoValid.json"));
            assertTrue(evolutionService.renameEntity(smo).equals("entity renamed"));
            smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/RenameEntitySmoIncompleteParam.json"));
            evolutionService.renameEntity(smo);
            fail();
        } catch (InputParameterException exception) {
            assertTrue(exception.getMessage().contains("Missing parameter"));
        }
    }

    @Test
    public void testRenameEntityIgnoreCase() throws IOException {
        try {
            smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/RenameEntitySmoValidIgnoreCase.json"));
            assertTrue(evolutionService.renameEntity(smo).equals("entity renamed"));
        } catch (InputParameterException exception) {
            System.out.println(exception);
            fail();
        }
    }

    /**
     * MIGRATE ENTITY
     */
//
//    @Test public void testMigrateEntity() throws IOException {
//        Entity expectedEntityToMigrate = new Entity("Client");
//        expectedEntityToMigrate.addAttribute("id", "int");
//        expectedEntityToMigrate.addAttribute("name","string");
//        expectedEntityToMigrate.addAttribute("city", "string");
//        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/MigrateEntitySmoValid.json"));
//        when(typhonDLConnection.isDatabaseRunning("mongodb", "myDocDB")).thenReturn(true);
//        evolutionService.migrateEntity(smo);
//        verify(typhonInterface).createEntity(expectedEntityToMigrate);
//        verify(typhonDLConnection).isDatabaseRunning("mongodb", "myDocDB");
//        verify(typhonInterface).readEntityData("Client");
//
//    }
}
