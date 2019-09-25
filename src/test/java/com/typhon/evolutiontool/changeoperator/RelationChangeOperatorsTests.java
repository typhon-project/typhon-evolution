package com.typhon.evolutiontool.changeoperator;

import com.typhon.evolutiontool.InitialTest;
import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.RelationDO;
import com.typhon.evolutiontool.entities.SMOAdapter;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Test;
import typhonml.*;

import static org.junit.Assert.*;

public class RelationChangeOperatorsTests extends InitialTest {

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
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/removeRelationChangeOperator.xmi");
        RemoveRelation removeRelation = (RemoveRelation) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(removeRelation);

        targetModel = evolutionService.evolveRelation(smo, sourceModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/removeRelationChangeOperator_final.xmi");

        Relation removedRelation = typhonMLInterface.getRelationFromNameInEntity(removeRelation.getRelationToRemove().getName(), ((Entity) removeRelation.getRelationToRemove().eContainer()).getName(), targetModel);
        assertNull(removedRelation);
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
}
