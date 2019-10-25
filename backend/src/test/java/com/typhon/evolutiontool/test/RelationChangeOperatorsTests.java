package com.typhon.evolutiontool.test;

import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.RelationDO;
import com.typhon.evolutiontool.entities.SMOAdapter;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Assert;
import org.junit.Test;
import typhonml.*;

public class RelationChangeOperatorsTests extends InitialTest {

    @Test
    public void testAddRelationChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/addRelationChangeOperator.xmi");
        AddRelation addRelation = (AddRelation) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addRelation);

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/addRelationChangeOperator_final.xmi");

        Assert.assertNull(null);
    }

    @Test
    public void testRemoveRelationship() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/removeRelationChangeOperator.xmi");
        RemoveRelation removeRelation = (RemoveRelation) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(removeRelation);

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/removeRelationChangeOperator_final.xmi");

        Relation removedRelation = typhonMLInterface.getRelationFromNameInEntity(removeRelation.getRelationToRemove().getName(), ((Entity) removeRelation.getRelationToRemove().eContainer()).getName(), targetModel);
        Assert.assertNull(removedRelation);
    }

    @Test
    public void testEnableRelationContainmentChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/enableRelationContainmentChangeOperator.xmi");
        EnableRelationContainment enableRelationContainment = (EnableRelationContainment) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(enableRelationContainment);

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/enableRelationContainmentChangeOperator_final.xmi");

        Relation updatedRelation = typhonMLInterface.getRelationFromNameInEntity(enableRelationContainment.getRelation().getName(), ((Entity) enableRelationContainment.getRelation().eContainer()).getName(), targetModel);
        Assert.assertNotEquals(enableRelationContainment.getRelation().getIsContainment(), updatedRelation.getIsContainment());
    }

    @Test
    public void testDisableRelationContainmentChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/disableRelationContainmentChangeOperator.xmi");
        DisableRelationContainment disableRelationContainment = (DisableRelationContainment) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(disableRelationContainment);

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/disableRelationContainmentChangeOperator_final.xmi");

        Relation updatedRelation = typhonMLInterface.getRelationFromNameInEntity(disableRelationContainment.getRelation().getName(), ((Entity) disableRelationContainment.getRelation().eContainer()).getName(), targetModel);
        Assert.assertNotEquals(disableRelationContainment.getRelation().getIsContainment(), updatedRelation.getIsContainment());
    }

    @Test
    public void testChangeRelationContainmentChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/changeRelationContainmentChangeOperator.xmi");
        ChangeRelationContainement changeRelationContainement = (ChangeRelationContainement) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(changeRelationContainement);

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/changeRelationContainmentChangeOperator_final.xmi");

        RelationDO relationDO = RelationDOFactory.buildInstance(changeRelationContainement.getRelation(), false);
        Relation updatedRelation = typhonMLInterface.getRelationFromNameInEntity(relationDO.getName(), relationDO.getSourceEntity().getName(), targetModel);
        Assert.assertNotEquals(relationDO.isContainment(), updatedRelation.getIsContainment());
    }

    @Test
    public void testEnableRelationOppositionChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/enableRelationOppositeChangeOperator.xmi");
        EnableBidirectionalRelation enableBidirectionalRelation = (EnableBidirectionalRelation) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(enableBidirectionalRelation);

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/enableRelationOppositeChangeOperator_final.xmi");

        String newOppositeRelationName = enableBidirectionalRelation.getRelation().getName().concat("_opposite");
        Relation newOppositeRelation = typhonMLInterface.getRelationFromNameInEntity(newOppositeRelationName, "CreditCard", targetModel);
        Assert.assertNotNull(newOppositeRelation);
        Assert.assertEquals(newOppositeRelation.getName(), newOppositeRelationName);
        Assert.assertEquals(newOppositeRelation.getCardinality().getValue(), Cardinality.ONE_MANY.getValue());
    }

    @Test
    public void testDisableRelationOppositionChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/disableRelationOppositeChangeOperator.xmi");
        DisableBidirectionalRelation disableBidirectionalRelation = (DisableBidirectionalRelation) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(disableBidirectionalRelation);

        RelationDO relation = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ParametersKeyString.RELATION), false);
        RelationDO oppositeRelation = relation.getOpposite();

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/disableRelationOppositeChangeOperator_final.xmi");

        Assert.assertNull(typhonMLInterface.getRelationFromNameInEntity(relation.getName(), relation.getSourceEntity().getName(), targetModel).getOpposite());
        Assert.assertNull(typhonMLInterface.getRelationFromNameInEntity(oppositeRelation.getName(), oppositeRelation.getSourceEntity().getName(), targetModel));
    }

    @Test
    public void testRenameRelationChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/renameRelationChangeOperator.xmi");
        RenameRelation renameRelation = (RenameRelation) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(renameRelation);

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/renameRelationChangeOperator_final.xmi");

        Relation renamedRelation = typhonMLInterface.getRelationFromNameInEntity(renameRelation.getNewRelationName(), ((Entity) renameRelation.getRelationToRename().eContainer()).getName(), targetModel);
        Assert.assertNotNull(renamedRelation);
        Assert.assertEquals(renamedRelation.getName(), renameRelation.getNewRelationName());
    }

    @Test
    public void testChangeRelationCardinalityChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/changeCardinalityRelationChangeOperator.xmi");
        ChangeRelationCardinality changeRelationCardinality = (ChangeRelationCardinality) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(changeRelationCardinality);

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/changeCardinalityRelationChangeOperator_final.xmi");

        Relation updatedRelation = typhonMLInterface.getRelationFromNameInEntity(changeRelationCardinality.getRelation().getName(), ((Entity) changeRelationCardinality.getRelation().eContainer()).getName(), targetModel);
        Assert.assertNotNull(updatedRelation);
        Assert.assertEquals(updatedRelation.getCardinality(), changeRelationCardinality.getNewCardinality());
    }
}
