package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.dummy.WorkingSetDummyImpl;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.TyphonInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EvolutionServiceTest {

    @Mock
    TyphonDLInterface typhonDLConnection;
    @Mock
    TyphonInterface typhonInterface;
    @Mock
    TyphonMLInterface typhonMLInterface;
    @InjectMocks
    EvolutionServiceImpl evolutionService= new EvolutionServiceImpl();
    private ObjectMapper mapper = new ObjectMapper();
    private SMO smo;

    /*
     ** CREATE ENTITY
     */


//    @Test
//    public void testCreateEntity() throws IOException {
//        try {
//            smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid.json"));
//            evolutionService.addEntityType(smo);
//            verify(typhonDLConnection).isDatabaseRunning(anyString(), anyString());
//            verify(typhonInterface).createEntityType(any(Entity.class),anyString());
//            verify(typhonMLInterface).setNewTyphonMLModel(anyString());
//            smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoIncompleteParam.json"));
//            evolutionService.addEntityType(smo);
//            fail();
//        } catch (InputParameterException exception) {
//            assertTrue(exception.getMessage().contains("Missing parameter"));
//        }
//    }
//
//
//    @Test
//    public void testVerifyTyphonDLStructureForCreateEntity() throws IOException, InputParameterException {
//        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid.json"));
//        //Database is running case
//        when(typhonDLConnection.isDatabaseRunning(smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString(), smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString())).thenReturn(true);
//        evolutionService.addEntityType(smo);
//        // Verify that isDatabaseRunning is called. And CreateDatabase is not called.
//        verify(typhonDLConnection, times(1)).isDatabaseRunning(smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString(),smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString());
//        verify(typhonDLConnection, times(0)).createDatabase(smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString(),smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString());
//        //Database is not running case
//        when(typhonDLConnection.isDatabaseRunning(smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString(), smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString())).thenReturn(false);
//        evolutionService.addEntityType(smo);
//        //Verify that createDatabase method is called.
//        verify(typhonDLConnection, times(1)).createDatabase(smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString(),smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString());
//    }
//
//
//
//    /*
//    ** RENAME ENTITY
//     */
//
//    @Test
//    public void testRenameEntity() throws IOException {
//        try {
//            smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/RenameEntitySmoValid.json"));
//            assertTrue(evolutionService.renameEntityType(smo).equals("entity renamed"));
//            verify(typhonInterface).renameEntity(smo.getInputParameter().get(ParametersKeyString.OLDENTITYNAME).toString(), smo.getInputParameter().get(ParametersKeyString.NEWENTITYNAME).toString(), smo.getInputParameter().get(ParametersKeyString.TARGETMODEL).toString());
//            verify(typhonMLInterface).setNewTyphonMLModel(smo.getInputParameter().get(ParametersKeyString.TARGETMODEL).toString());
//            smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/RenameEntitySmoIncompleteParam.json"));
//            evolutionService.renameEntityType(smo);
//            fail();
//        } catch (InputParameterException exception) {
//            assertTrue(exception.getMessage().contains("Missing parameter"));
//        }
//    }
//
//    @Test
//    public void testRenameEntityIgnoreCase() throws IOException {
//        try {
//            smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/RenameEntitySmoValidIgnoreCase.json"));
//            assertTrue(evolutionService.renameEntityType(smo).equals("entity renamed"));
//        } catch (InputParameterException exception) {
//            System.out.println(exception);
//            fail();
//        }
//    }
//
//    /**
//     * MIGRATE ENTITY
//     */
//
//    @Test
//    public void testMigrateEntity() throws IOException, InputParameterException {
//        String sourcemodelid, targetmodelid, databasetype, databasename, entity;
//
//        Entity expectedEntityToMigrate = new Entity("Client");
//        expectedEntityToMigrate.addAttribute("id", "int");
//        expectedEntityToMigrate.addAttribute("name","string");
//        expectedEntityToMigrate.addAttribute("city", "string");
//        EntityInstance entity1 = new EntityInstance("101");
//        entity1.addAttribute("name","Gobert");
//        entity1.addAttribute("city","Namur");
//        EntityInstance entity2 = new EntityInstance("3013");
//        entity2.addAttribute("name","Cleve");
//        entity2.addAttribute("city","Namur");
//        WorkingSet workingSetData = new WorkingSetDummyImpl();
//        ((WorkingSetDummyImpl) workingSetData).setEntityRows("Client", Arrays.asList(entity1, entity2));
//
//        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/MigrateEntitySmoValid.json"));
//        entity = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
//        targetmodelid = smo.getInputParameter().get(ParametersKeyString.TARGETMODEL).toString();
//        sourcemodelid = smo.getInputParameter().get(ParametersKeyString.SOURCEMODEL).toString();
//        databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
//        databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();
//
//        when(typhonDLConnection.isDatabaseRunning(databasetype,databasename)).thenReturn(true);
//        when(typhonMLInterface.getEntityTypeFromName(entity, sourcemodelid)).thenReturn(expectedEntityToMigrate);
//        when(typhonInterface.readAllEntityData(expectedEntityToMigrate,sourcemodelid)).thenReturn(workingSetData);
//        evolutionService.migrateEntity(smo);
//        verify(typhonInterface).createEntityType(expectedEntityToMigrate, smo.getInputParameter().get(ParametersKeyString.TARGETMODEL).toString());
//        verify(typhonDLConnection).isDatabaseRunning("documentdb", "MongoDB");
//        verify(typhonInterface).writeWorkingSetData(workingSetData,targetmodelid);
//    }

}
