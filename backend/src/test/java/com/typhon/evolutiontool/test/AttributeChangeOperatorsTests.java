package com.typhon.evolutiontool.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMOAdapter;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;

import typhonml.AddAttribute;
import typhonml.Attribute;
import typhonml.ChangeAttributeType;
import typhonml.Entity;
import typhonml.RemoveAttribute;
import typhonml.RenameAttribute;

public class AttributeChangeOperatorsTests extends InitialTest {

    @Test
    public void testAddAttributeChangeOperator() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("test/resources/addAttributeChangeOperator.xmi");
        AddAttribute addAttribute = (AddAttribute) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addAttribute);

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
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/removeAttributeChangeOperator_final.xmi");
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
