package com.typhon.evolutiontool.test;


import com.typhon.evolutiontool.entities.SMOAdapter;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Test;
import typhonml.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class EntityChangeOperatorsTests extends InitialTest {

    @Test
    public void testCreateEntityChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
        //Change Operator create entity
        AddEntity addEntity = TyphonmlFactory.eINSTANCE.createAddEntity();
        addEntity.setName("NEWENTITY");
        Attribute attribute = TyphonmlFactory.eINSTANCE.createAttribute();
        attribute.setName("attribute");
        attribute.setType(addEntity);
        addEntity.getAttributes().add(attribute);
        //TODO by TyphonML Missing other required parameters in AddEntity ChangeOperator (databasename, targetlogicalname, etc...)
        sourceModel.getChangeOperators().add(addEntity);

        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addEntity);
        targetModel = evolutionService.evolveEntity(smo, sourceModel);
        assertNotNull(typhonMLInterface.getEntityTypeFromName("NEWENTITY", targetModel));
    }

    @Test
    public void testRemoveEntityTypeChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
        RemoveEntity removeEntity = TyphonmlFactory.eINSTANCE.createRemoveEntity();
        removeEntity.setEntityToRemove(typhonMLInterface.getEntityTypeFromName("User", sourceModel));
        sourceModel.getChangeOperators().add(removeEntity);
        TyphonMLUtils.saveModel(sourceModel, "resources/tml_removeEntityChangeOp.xmi");

        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(removeEntity);
        targetModel = evolutionService.evolveEntity(smo, sourceModel);
        assertNotNull(typhonMLInterface.getEntityTypeFromName("User", sourceModel));
        assertNull(typhonMLInterface.getEntityTypeFromName("User", targetModel));
    }

    @Test
    public void testRenameEntityChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/renameEntityChangeOperator.xmi");
        RenameEntity renameEntity = (RenameEntity) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(renameEntity);

        targetModel = evolutionService.evolveEntity(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/renameEntityChangeOperator_final.xmi");
    }

//    @Test
//    public void testSplitHorizontalChangeOperator() {
//        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
//        SplitEntity splitEntity = TyphonmlFactory.eINSTANCE.createSplitEntity();
//        splitEntity.setEntityToBeSplit(typhonMLInterface.getEntityTypeFromName("Order", sourceModel));
//        //TODO
//    }

    @Test
    public void testMigrateEntityChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/migrateEntityChangeOperator.xmi");
        MigrateEntity migrateEntity = (MigrateEntity) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(migrateEntity);

        targetModel = evolutionService.evolveEntity(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/migrateEntityChangeOperator_final.xmi");
    }
}
