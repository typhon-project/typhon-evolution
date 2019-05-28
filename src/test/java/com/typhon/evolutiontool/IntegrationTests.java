package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import com.typhon.evolutiontool.services.TyphonInterface;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonQL.TyphonInterfaceQLImpl;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Before;
import org.junit.Test;
import typhonml.Model;

import java.io.File;
import java.io.IOException;

public class IntegrationTests {

    TyphonDLInterface typhonDLInterface = new TyphonDLInterfaceImpl();
    TyphonInterface typhonInterface = new TyphonInterfaceQLImpl();
    TyphonMLInterface typhonMLInterface = new TyphonMLInterfaceImpl();
    EvolutionServiceImpl evolutionService = new EvolutionServiceImpl();
    private ObjectMapper mapper = new ObjectMapper();
    private SMO smo;
    public static Model sourceModel, targetModel;
//    public static final String sourcemodelpath = "resources/baseModel.xmi";
    public static final String sourcemodelpath = "resources/generated_demo.xmi";
    public static final String finalModelPath = "resources/finalModel.xmi";

    @Before
    public void setUp() {
        TyphonMLUtils.typhonMLPackageRegistering();
        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
        evolutionService.setTyphonDLInterface(typhonDLInterface);
        evolutionService.setTyphonInterface(typhonInterface);
        evolutionService.setTyphonMLInterface(typhonMLInterface);
    }

    /**
     * Manual verification of produced model.
     */
    @Test
    public void testCreateEntity() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValidTyphonML.json"));

        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
        targetModel = evolutionService.addEntityType(smo,sourceModel);
    }

    /**
     * Manual verification of produced model.
     */
    @Test
    public void testRemoveEntity() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/RemoveEntitySmoValidTyphonML.json"));

        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
        targetModel = evolutionService.removeEntityType(smo,sourceModel);
        TyphonMLUtils.saveModel(targetModel,finalModelPath);
    }


    /**
     * Manual verification of produced model.
     */
    @Test
    public void testRenameEntity() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/RenameEntitySmo.json"));

        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
        targetModel = evolutionService.renameEntityType(smo,sourceModel);
        TyphonMLUtils.saveModel(targetModel,finalModelPath);
    }


}
