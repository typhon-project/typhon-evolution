package com.typhon.evolutiontool.test;

import com.typhon.evolutiontool.entities.SMOAdapter;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Assert;
import org.junit.Test;
import typhonml.*;

public class AttributeChangeOperatorsTests extends InitialTest {

    @Test
    //TODO: test the evolution operator after TyphonML has uncommented it (problem with the type)
    public void testAddAttributeChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/addAttributeChangeOperator.xmi");
        AddAttribute addAttribute = (AddAttribute) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addAttribute);

        targetModel = evolutionService.evolveAttribute(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/addAttributeChangeOperator_final.xmi");

        Entity updatedEntity = evolutionService.getTyphonMLInterface().getEntityByName(addAttribute.getOwnerEntity().getName(), targetModel);
        EntityAttributeKind addedAttribute = updatedEntity.getAttributes()
                .stream()
                .filter(attr -> addAttribute.getName().equals(attr.getName()))
                .findAny()
                .orElse(null);
        Assert.assertNotNull(addedAttribute);
    }

    @Test
    public void testRemoveAttributeChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/removeAttributeChangeOperator.xmi");
        RemoveAttribute removeAttribute = (RemoveAttribute) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(removeAttribute);

        targetModel = evolutionService.evolveAttribute(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/removeAttributeChangeOperator_final.xmi");
    }

    @Test
    public void testRenameAttributeChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/renameAttributeChangeOperator.xmi");
        RenameAttribute renameAttribute = (RenameAttribute) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(renameAttribute);

        targetModel = evolutionService.evolveAttribute(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/renameAttributeChangeOperator_final.xmi");
    }

    @Test
    //TODO: test the evolution operator after TyphonML has uncommented it (problem with the type)
    public void testChangeTypeAttributeChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/changeTypeAttributeChangeOperator.xmi");
        ChangeAttributeType changeAttributeType = (ChangeAttributeType) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(changeAttributeType);

        targetModel = evolutionService.evolveAttribute(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/changeTypeAttributeChangeOperator_final.xmi");
    }
}
