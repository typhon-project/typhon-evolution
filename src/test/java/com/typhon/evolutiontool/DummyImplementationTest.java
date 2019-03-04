package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.dummy.DummyImplementation;
import com.typhon.evolutiontool.dummy.WorkingSetDummyImpl;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.TyphonDLSchema;
import com.typhon.evolutiontool.entities.TyphonMLSchema;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import com.typhon.evolutiontool.services.TyphonInterface;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import static junit.framework.TestCase.*;

//@RunWith(SpringRunner.class)
//@DataMongoTest
public class DummyImplementationTest {

    TyphonDLInterface typhonDLInterface;
    TyphonInterface typhonInterface ;
    TyphonMLInterface typhonMLInterface ;
    EvolutionServiceImpl evolutionService = new EvolutionServiceImpl();
    private ObjectMapper mapper = new ObjectMapper();
    private SMO smo;
    TyphonMLSchema schemasource, schematarget;
//    @Autowired
//    MongoTemplate mongoTemplate;
    // Line below is to run with the real MongoDB server
    //@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
//    @Autowired
//    TyphonMongoRepository typhonMongoRepository;

    @Before
    public void setUp() throws IOException {
        DummyImplementation dummyImplementation = new DummyImplementation();
        typhonDLInterface = dummyImplementation;
        typhonInterface = dummyImplementation;
        typhonMLInterface = dummyImplementation;
        evolutionService.setTyphonDLInterface(typhonDLInterface);
        evolutionService.setTyphonInterface(typhonInterface);
        evolutionService.setTyphonMLInterface(typhonMLInterface);
        TyphonDLSchema typhonDLSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_V1.json"));
        mapper.writerFor(TyphonDLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonDL_Current.json"), typhonDLSchema);
        TyphonMLSchema typhonMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V1_CreateEntitySMO.json"));
        mapper.writerFor(TyphonMLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonML_Current.json"), typhonMLSchema);
    }

//    @Test
//    public void testCreateEntity() throws IOException, InputParameterException {
//        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid.json"));
//        schematarget = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V2.json"));
//
//        evolutionService.addEntity(smo);
//        TyphonMLSchema retrievedSchemaTarget = typhonMongoRepository.findByVersion("TyphonML_V2");
//    }

    @Test
    public void testFakeTyphonDLInterfaceIsDatabaseRunning(){
        assertFalse(typhonDLInterface.isDatabaseRunning("dummy","dummy"));
        assertTrue(typhonDLInterface.isDatabaseRunning("documentdb","MongoDB"));
    }

    @Test
    public void testFakeTyphonDLInterfaceCreateDatabase() throws IOException {
        TyphonDLSchema expectedSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_V2.json"));
        typhonDLInterface.createDatabase("relationaldb", "MySQL");
        TyphonDLSchema retrievedSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_Current.json"));
        assertTrue(expectedSchema.getRunningdb().containsAll(retrievedSchema.getRunningdb()));
        assertTrue(retrievedSchema.getRunningdb().containsAll(expectedSchema.getRunningdb()));
    }

    @Test
    public void testWorkingSetJSON() throws IOException {
        WorkingSetDummyImpl workingSetDummy = new WorkingSetDummyImpl();
        workingSetDummy.setRows(mapper.readerFor(LinkedHashMap.class).readValue(new File("src/main/resources/test/WorkingSetData.json")));
        assertNotNull(workingSetDummy);
    }

    @Test
    public void testCreateEntity() throws IOException, InputParameterException {
        TyphonDLSchema typhonDLSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_V1.json"));
        mapper.writerFor(TyphonDLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonDL_Current.json"), typhonDLSchema);
        TyphonMLSchema typhonMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V1_CreateEntitySMO.json"));
        mapper.writerFor(TyphonMLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonML_Current.json"), typhonMLSchema);

        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid.json"));
        evolutionService.addEntity(smo);
        TyphonDLSchema expectedDLSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_V2.json"));
        TyphonDLSchema retrievedSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_Current.json"));
        TyphonDLSchema falseSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_V1.json"));
        assertEquals(expectedDLSchema,retrievedSchema);
        assertFalse(falseSchema.equals(retrievedSchema));
        TyphonMLSchema expectedMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V2.json"));
        TyphonMLSchema retrievedMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_Current.json"));
        assertEquals(expectedMLSchema, retrievedMLSchema);

    }

    @Test
    public void testRenameEntity() throws IOException, InputParameterException {
        TyphonDLSchema typhonDLSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_V2.json"));
        mapper.writerFor(TyphonDLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonDL_Current.json"), typhonDLSchema);
        TyphonMLSchema typhonMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V1_RenameEntitySMO.json"));
        mapper.writerFor(TyphonMLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonML_Current.json"), typhonMLSchema);


        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/RenameEntitySmoValid.json"));
        evolutionService.renameEntity(smo);
        TyphonDLSchema expectedDLSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_V2.json"));
        TyphonDLSchema retrievedSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_Current.json"));
        TyphonDLSchema falseSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_V1.json"));
        assertEquals(expectedDLSchema,retrievedSchema);
        assertFalse(falseSchema.equals(retrievedSchema));
        TyphonMLSchema expectedMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V2.json"));
        TyphonMLSchema retrievedMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_Current.json"));
        assertEquals(expectedMLSchema, retrievedMLSchema);
        assertNotNull(retrievedMLSchema.getEntityFromName("client"));
        assertNull(retrievedMLSchema.getEntityFromName("user"));


    }

    @Test
    public void testMigrateEntity() throws IOException, InputParameterException {
        TyphonDLSchema typhonDLSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_V1_MigrateEntity.json"));
        mapper.writerFor(TyphonDLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonDL_Current.json"), typhonDLSchema);
        TyphonMLSchema typhonMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V1_MigrateEntity.json"));
        mapper.writerFor(TyphonMLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonML_Current.json"), typhonMLSchema);


        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/MigrateEntitySmoValid.json"));
        evolutionService.migrateEntity(smo);
        TyphonDLSchema expectedDLSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_V2.json"));
        TyphonDLSchema retrievedSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_Current.json"));
        assertEquals(expectedDLSchema,retrievedSchema);
        TyphonMLSchema expectedMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V2_MigrateEntity.json"));
        TyphonMLSchema retrievedMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_Current.json"));
        assertEquals(expectedMLSchema, retrievedMLSchema);
    }

    @After
    public void resetFiles() throws IOException {
        TyphonDLSchema typhonDLSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("src/main/resources/test/TyphonDL_V1.json"));
        mapper.writerFor(TyphonDLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonDL_Current.json"), typhonDLSchema);
        TyphonMLSchema typhonMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V1_CreateEntitySMO.json"));
        mapper.writerFor(TyphonMLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonML_Current.json"), typhonMLSchema);
    }

}
