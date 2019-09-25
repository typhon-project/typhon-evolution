package com.typhon.evolutiontool;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Test;
import typhonml.*;

import java.util.List;

import static org.junit.Assert.*;

public class ChangeOperatorsTest extends InitialTest {


    @Test
    public void testReadChangeOperators() {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/complexModelWithChangeOperators.xmi");
        List<ChangeOperator> changeOperatorList = sourceModel.getChangeOperators();
        ChangeOperator changeOperator;
        changeOperator = changeOperatorList.get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(changeOperator);
        assertEquals(TyphonMLObject.ENTITY, smo.getTyphonObject());
        assertEquals(EvolutionOperator.REMOVE, smo.getEvolutionOperator());

        changeOperator = changeOperatorList.get(1);
        SMOAdapter smo2 = SMOFactory.createSMOAdapterFromChangeOperator(changeOperator);
        assertEquals(TyphonMLObject.ENTITY, smo2.getTyphonObject());
        assertEquals(EvolutionOperator.RENAME, smo2.getEvolutionOperator());
    }

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
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
        RenameEntity renameEntity = TyphonmlFactory.eINSTANCE.createRenameEntity();
        renameEntity.setEntityToRename(typhonMLInterface.getEntityTypeFromName("User", sourceModel));
        renameEntity.setNewEntityName("CUSTOMER");

        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(renameEntity);
        targetModel = evolutionService.evolveEntity(smo, sourceModel);
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
    public void testMigrateEntityChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
        MigrateEntity migrateEntity = TyphonmlFactory.eINSTANCE.createMigrateEntity();
        migrateEntity.setEntity(typhonMLInterface.getEntityTypeFromName("User", sourceModel));
        migrateEntity.setNewDatabase(typhonMLInterface.getDatabaseFromName("MongoDB", sourceModel));

        assertNotEquals("MongoDB", typhonMLInterface.getDatabaseName("User", targetModel));
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(migrateEntity);
        targetModel = evolutionService.evolveEntity(smo, sourceModel);
        assertEquals("MongoDB", typhonMLInterface.getDatabaseName("User", targetModel));
    }

    @Test
    public void testCreateRelationshipChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/generated_demo.xmi");
        AddRelation addRelation = TyphonmlFactory.eINSTANCE.createAddRelation();
        addRelation.setName("ADDEDRELATION");
        addRelation.setType(typhonMLInterface.getEntityTypeFromName("Order", sourceModel));
        addRelation.setIsContainment(false);
        //TODO by TyphonML Missing sourceEntity info in AddRelation ChnageOperator.
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addRelation);
        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        assertNotNull(typhonMLInterface.getRelationFromNameInEntity("ADDEDRELATION", "User", targetModel));
    }

    @Test
    public void testRemoveRelationship() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/complexModelWithChangeOperators.xmi");
        RemoveRelation removeRelation = TyphonmlFactory.eINSTANCE.createRemoveRelation();
        removeRelation.setRelationToRemove(typhonMLInterface.getRelationFromNameInEntity("paidWith", "Order", sourceModel));
        //TODO by TyphonML Missing sourceEntity info in AddRelation ChangeOperator.
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(removeRelation);
        assertNotNull(typhonMLInterface.getRelationFromNameInEntity("paidWith", "Order", sourceModel));
        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        assertNull(typhonMLInterface.getRelationFromNameInEntity("paidWith", "Order", targetModel));
    }

    @Test
    public void testEnableRelationOppositionChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/enableRelationOppositeChangeOperator.xmi");
        EnableBidirectionalRelation enableBidirectionalRelation = (EnableBidirectionalRelation) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(enableBidirectionalRelation);

        // Actually not working because missing parameter "relationname"
        // Work around to succeed the test:
//        smo.getInputParameter().put(ParametersKeyString.RELATIONNAME, "newOppositeRelation");
        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/enableRelationOppositeChangeOperator_final.xmi");

        Relation newOppositeRelation = typhonMLInterface.getRelationFromNameInEntity("newOppositeRelation", "CreditCard", targetModel);
        assertNotNull(newOppositeRelation);
        assertEquals(newOppositeRelation.getName(), "newOppositeRelation");
        assertEquals(newOppositeRelation.getCardinality().getValue(), Cardinality.ONE_MANY.getValue());
    }

    @Test
    public void testDisableRelationOppositionChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/disableRelationOppositeChangeOperator.xmi");
        DisableBidirectionalRelation disableBidirectionalRelation = (DisableBidirectionalRelation) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(disableBidirectionalRelation);

        RelationDO relation = smo.getRelationDOFromInputParameter(ParametersKeyString.RELATION);
        RelationDO oppositeRelation = relation.getOpposite();

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/disableRelationOppositeChangeOperator_final.xmi");

        assertNull(typhonMLInterface.getRelationFromNameInEntity(relation.getName(), relation.getSourceEntity().getName(), targetModel).getOpposite());
        assertNull(typhonMLInterface.getRelationFromNameInEntity(oppositeRelation.getName(), oppositeRelation.getSourceEntity().getName(), targetModel));
    }

    @Test
    public void testRenameRelationChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/renameRelationChangeOperator.xmi");
        RenameRelation renameRelation = (RenameRelation) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(renameRelation);

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/renameRelationChangeOperator_final.xmi");

        Relation renamedRelation = typhonMLInterface.getRelationFromNameInEntity(renameRelation.getNewRelationName(), ((Entity) renameRelation.getRelationToRename().eContainer()).getName(), targetModel);
        assertNotNull(renamedRelation);
        assertEquals(renamedRelation.getName(), renameRelation.getNewRelationName());
    }

    @Test
    public void testChangeRelationCardinalityChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/changeCardinalityRelationChangeOperator.xmi");
        ChangeRelationCardinality changeRelationCardinality = (ChangeRelationCardinality) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(changeRelationCardinality);

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/changeCardinalityRelationChangeOperator_final.xmi");

        Relation updatedRelation = typhonMLInterface.getRelationFromNameInEntity(changeRelationCardinality.getRelation().getName(), ((Entity) changeRelationCardinality.getRelation().eContainer()).getName(), targetModel);
        assertNotNull(updatedRelation);
        assertEquals(updatedRelation.getCardinality(), changeRelationCardinality.getNewCardinality());
    }

    @Test
    public void testAddAttributeChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/addAttributeChangeOperator.xmi");
        AddAttribute addAttribute = (AddAttribute) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addAttribute);
        // Work around to succeed the test:
//        smo.getInputParameter().put(ParametersKeyString.ENTITYNAME, "CreditCard");

        targetModel = evolutionService.evolveAttribute(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/addAttributeChangeOperator_final.xmi");

        Entity updatedEntity = typhonMLInterface.getEntityTypeFromName(String.valueOf(smo.getInputParameter().get(ParametersKeyString.ENTITYNAME)), targetModel);
        Attribute addedAttribute = updatedEntity.getAttributes()
                .stream()
                .filter(attr -> smo.getInputParameter().get(ParametersKeyString.ATTRIBUTENAME).equals(attr.getName()))
                .findAny()
                .orElse(null);
        assertNotNull(addedAttribute);
    }

    @Test
    public void testRemoveAttributeChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/removeAttributeChangeOperator.xmi");
        RemoveAttribute removeAttribute = (RemoveAttribute) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(removeAttribute);
        // Work around to succeed the test:
//        smo.getInputParameter().put(ParametersKeyString.ENTITYNAME, "CreditCard");

        targetModel = evolutionService.evolveAttribute(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/removeAttributeChangeOperator.xmi_final.xmi");
    }

    @Test
    public void testRenameAttributeChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/renameAttributeChangeOperator.xmi");
        RenameAttribute renameAttribute = (RenameAttribute) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(renameAttribute);
        // Work around to succeed the test:
//        smo.getInputParameter().put(ParametersKeyString.ENTITYNAME, "CreditCard");

        targetModel = evolutionService.evolveAttribute(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/renameAttributeChangeOperator_final.xmi");
    }

    @Test
    public void testChangeTypeAttributeChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/changeTypeAttributeChangeOperator.xmi");
        ChangeAttributeType changeAttributeType = (ChangeAttributeType) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(changeAttributeType);
        // Work around to succeed the test:
//        smo.getInputParameter().put(ParametersKeyString.ENTITYNAME, "CreditCard");

        targetModel = evolutionService.evolveAttribute(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/changeTypeAttributeChangeOperator_final.xmi");
    }
}
