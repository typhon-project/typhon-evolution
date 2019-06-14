package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.services.TyphonInterface;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonQL.TyphonInterfaceQLImpl;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Before;
import org.junit.Test;
import typhonml.ChangeOperator;
import typhonml.Model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class IntegrationTests extends InitialTest{

    /**
     * Manual verification of produced model.
     */
    @Test
    public void testCreateEntity() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMOJsonImpl.class).readValue(new File("src/main/resources/test/CreateEntitySmoValidTyphonML.json"));

        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
        targetModel = evolutionService.addEntityType(smo,sourceModel);
        TyphonMLUtils.saveModel(targetModel,finalModelPath);
    }

    /**
     * Manual verification of produced model.
     */
    @Test
    public void testRemoveEntity() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMOJsonImpl.class).readValue(new File("src/main/resources/test/RemoveEntitySmoValidTyphonML.json"));

        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
        targetModel = evolutionService.removeEntityType(smo,sourceModel);
        TyphonMLUtils.saveModel(targetModel,finalModelPath);
    }


    /**
     * Manual verification of produced model.
     */
    @Test
    public void testRenameEntity() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMOJsonImpl.class).readValue(new File("src/main/resources/test/RenameEntitySmo.json"));

        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
        targetModel = evolutionService.renameEntityType(smo,sourceModel);
        TyphonMLUtils.saveModel(targetModel,finalModelPath);
    }


    /**
     * Manual verification of produced model.
     */
    @Test
    public void testSplitHorizontal() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMOJsonImpl.class).readValue(new File("src/main/resources/test/SplitHorizontalSmo.json"));

        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
        targetModel = evolutionService.splitHorizontal(smo,sourceModel);
        TyphonMLUtils.saveModel(targetModel,finalModelPath);
    }

    /**
     * Manual verification of produced model.
     */
    @Test
    public void testMigrateEntity() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMOJsonImpl.class).readValue(new File("src/main/resources/test/MigrateEntitySmo.json"));

        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
        targetModel = evolutionService.migrateEntity(smo,sourceModel);
        TyphonMLUtils.saveModel(targetModel,finalModelPath);
    }

    /**
     * Manual verification of produced model.
     */
    @Test
    public void testDeleteRelationship() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMOJsonImpl.class).readValue(new File("src/main/resources/test/DeleteRelationSmo.json"));

        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/complexModelWithChangeOperators.xmi");
        targetModel = evolutionService.removeRelationship(smo,sourceModel);
        TyphonMLUtils.saveModel(targetModel,finalModelPath);
    }

    /**
     * Manual verification of produced model.
     */
    @Test
    public void testCreateRelation() throws IOException, InputParameterException {
        smo = mapper.readerFor(SMOJsonImpl.class).readValue(new File("src/main/resources/test/CreateRelationSmo.json"));

        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/complexModelWithChangeOperators.xmi");
        targetModel = evolutionService.addRelationship(smo,sourceModel);
        TyphonMLUtils.saveModel(targetModel,finalModelPath);
    }

    @Test
    public void testExecutionChangeOperator() throws InputParameterException {
        Model targetModel;
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/complexModelWithChangeOperators.xmi");
        List<ChangeOperator> changeOperatorList = sourceModel.getChangeOperators();
        ChangeOperator changeOperator;
        changeOperator = changeOperatorList.get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(changeOperator);
        assertEquals(TyphonMLObject.ENTITY,smo.getTyphonObject());
        assertEquals(EvolutionOperator.REMOVE, smo.getEvolutionOperator());
        targetModel = evolutionService.renameEntityType(smo, sourceModel);
        assertNotNull(typhonMLInterface.getEntityTypeFromName("Basciani", sourceModel));
        assertNull(typhonMLInterface.getEntityTypeFromName("Basciani",targetModel));
    }


}
