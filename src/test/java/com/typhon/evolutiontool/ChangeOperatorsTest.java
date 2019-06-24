package com.typhon.evolutiontool;

import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMOAdapter;
import com.typhon.evolutiontool.entities.TyphonMLObject;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Before;
import org.junit.Test;
import typhonml.*;

import java.util.List;

import static org.junit.Assert.*;

public class ChangeOperatorsTest extends InitialTest{


    @Test
    public void testReadChangeOperators(){
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/complexModelWithChangeOperators.xmi");
        List<ChangeOperator> changeOperatorList = sourceModel.getChangeOperators();
        ChangeOperator changeOperator;
        changeOperator = changeOperatorList.get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(changeOperator);
        assertEquals(TyphonMLObject.ENTITY, smo.getTyphonObject());
        assertEquals(EvolutionOperator.REMOVE, smo.getEvolutionOperator());

        changeOperator = changeOperatorList.get(1);
        SMOAdapter smo2 = SMOFactory.createSMOAdapterFromChangeOperator(changeOperator);
        assertEquals(TyphonMLObject.ENTITY,smo2.getTyphonObject());
        assertEquals(EvolutionOperator.RENAME, smo2.getEvolutionOperator());


    }

    @Test
    public void testCreateEntityChangeOperator() throws InputParameterException {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
        //Change Operator create entity
        AddEntity addEntity = TyphonmlFactory.eINSTANCE.createAddEntity();
        addEntity.setName("NEWENTITY");
        Attribute attribute = TyphonmlFactory.eINSTANCE.createAttribute();
        attribute.setName("attribute");
        attribute.setType(addEntity);
        addEntity.getAttributes().add(attribute);
        //TODO Missing other required parameters in AddEntity ChangeOperator (databasename, targetlogicalname, etc...)
        sourceModel.getChangeOperators().add(addEntity);

        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addEntity);
        targetModel = evolutionService.addEntityType(smo, sourceModel);
        assertNotNull(typhonMLInterface.getEntityTypeFromName("NEWENTITY",targetModel));

    }

    @Test
    public void testRemoveEntityTypeChangeOperator() throws InputParameterException {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
        RemoveEntity removeEntity = TyphonmlFactory.eINSTANCE.createRemoveEntity();
        removeEntity.setEntityToRemove(typhonMLInterface.getEntityTypeFromName("User", sourceModel));

        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(removeEntity);
        targetModel = evolutionService.removeEntityType(smo, sourceModel);
        assertNotNull(typhonMLInterface.getEntityTypeFromName("User", sourceModel));
        assertNull(typhonMLInterface.getEntityTypeFromName("User", targetModel));
    }

    @Test
    public void testRenameEntityChangeOperator() throws InputParameterException {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
        RenameEntity renameEntity = TyphonmlFactory.eINSTANCE.createRenameEntity();
        renameEntity.setEntityToRename(typhonMLInterface.getEntityTypeFromName("User", sourceModel));
        renameEntity.setNewEntityName("CUSTOMER");

        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(renameEntity);
        targetModel = evolutionService.renameEntityType(smo, sourceModel);
        assertNotNull(typhonMLInterface.getEntityTypeFromName("User", sourceModel));
        assertNull(typhonMLInterface.getEntityTypeFromName("User", targetModel));
        assertNotNull(typhonMLInterface.getEntityTypeFromName("CUSTOMER", targetModel));
    }

//    @Test
//    public void testSplitHorizontalChangeOperator() {
//        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
//        SplitEntity splitEntity = TyphonmlFactory.eINSTANCE.createSplitEntity();
//        splitEntity.setEntityToBeSplit(typhonMLInterface.getEntityTypeFromName("Order", sourceModel));
//        //TODO
//    }

    @Test
    public void testMigrateEntityChangeOperator() throws InputParameterException {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
        MigrateEntity migrateEntity = TyphonmlFactory.eINSTANCE.createMigrateEntity();
        migrateEntity.setEntity(typhonMLInterface.getEntityTypeFromName("User", sourceModel));
        migrateEntity.setNewDatabase(typhonMLInterface.getDatabaseFromName("MongoDB",sourceModel));

        assertNotEquals("MongoDB", typhonMLInterface.getDatabaseName("User", targetModel));
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(migrateEntity);
        targetModel = evolutionService.migrateEntity(smo, sourceModel);
        assertEquals("MongoDB", typhonMLInterface.getDatabaseName("User", targetModel));
    }

    @Test
    public void testCreateRelationshipChangeOperator() throws InputParameterException {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
        AddRelation addRelation = TyphonmlFactory.eINSTANCE.createAddRelation();
        addRelation.setName("ADDEDRELATION");
        addRelation.setType(typhonMLInterface.getEntityTypeFromName("Order", sourceModel));
        addRelation.setIsContainment(false);
        //TODO Missing sourceEntity info in AddRelation ChnageOperator.
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addRelation);
        targetModel = evolutionService.addRelationship(smo, sourceModel);
        assertNotNull(typhonMLInterface.getRelationFromNameInEntity("ADDEDRELATION", "User",targetModel));
    }

    @Test
    public void testRemoveRelationship() {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/complexModelWithChangeOperators.xmi");
        RemoveRelation removeRelation = TyphonmlFactory.eINSTANCE.createRemoveRelation();
        removeRelation.setRelationToRemove(typhonMLInterface.getRelationFromNameInEntity("paidWith", "Order", sourceModel));

        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(removeRelation);
        assertNotNull(typhonMLInterface.getRelationFromNameInEntity("paidWith","Order",sourceModel));
        targetModel = evolutionService.removeRelationship(smo, sourceModel);
        assertNull(typhonMLInterface.getRelationFromNameInEntity("paidWith", "Order", targetModel));
    }

}
