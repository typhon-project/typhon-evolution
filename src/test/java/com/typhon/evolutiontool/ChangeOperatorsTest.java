package com.typhon.evolutiontool;

import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMOAdapter;
import com.typhon.evolutiontool.entities.TyphonMLObject;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Before;
import org.junit.Test;
import typhonml.*;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChangeOperatorsTest extends InitialTest{


    @Test
    public void testReadChangeOperators(){
        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
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
        sourceModel.getChangeOperators().add(addEntity);

        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addEntity);
        targetModel = evolutionService.addEntityType(smo, sourceModel);
        assertNotNull(typhonMLInterface.getEntityTypeFromName("NEWENTITY",targetModel));

    }

}
