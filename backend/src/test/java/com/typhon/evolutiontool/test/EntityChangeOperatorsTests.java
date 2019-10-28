package com.typhon.evolutiontool.test;


import com.typhon.evolutiontool.entities.SMOAdapter;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Test;
import typhonml.*;

public class EntityChangeOperatorsTests extends InitialTest {

    @Test
    public void testMigrateEntityChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/migrateEntityChangeOperator.xmi");
        MigrateEntity migrateEntity = (MigrateEntity) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(migrateEntity);

        targetModel = evolutionService.evolveEntity(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/migrateEntityChangeOperator_final.xmi");
    }

    @Test
    public void testRenameEntityChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/renameEntityChangeOperator.xmi");
        RenameEntity renameEntity = (RenameEntity) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(renameEntity);

        targetModel = evolutionService.evolveEntity(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/renameEntityChangeOperator_final.xmi");
    }

    @Test
    public void testRemoveEntityTypeChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/removeEntityChangeOperator.xmi");
        RemoveEntity removeEntity = (RemoveEntity) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(removeEntity);

        targetModel = evolutionService.evolveEntity(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/removeEntityChangeOperator_final.xmi");
    }

    @Test
    public void testAddEntityChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/addEntityChangeOperator.xmi");
        AddEntity addEntity = (AddEntity) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addEntity);

        targetModel = evolutionService.evolveEntity(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/addEntityChangeOperator_final.xmi");
    }

    @Test
    public void testSplitVerticalEntityChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/splitVerticalEntityChangeOperator.xmi");
        SplitEntity splitVerticalEntity = (SplitEntity) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(splitVerticalEntity);

        targetModel = evolutionService.evolveEntity(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/splitVerticalEntityChangeOperator_final.xmi");
    }

    @Test
    public void testSplitHorizontalEntityChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/splitHorizontalEntityChangeOperator.xmi");
        SplitEntity splitHorizontalEntity = (SplitEntity) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(splitHorizontalEntity);

        targetModel = evolutionService.evolveEntity(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/splitHorizontalEntityChangeOperator_final.xmi");
    }
}
