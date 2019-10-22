package test;


import org.junit.Test;

import main.java.com.typhon.evolutiontool.entities.EvolutionOperator;
import main.java.com.typhon.evolutiontool.entities.SMOAdapter;
import main.java.com.typhon.evolutiontool.entities.TyphonMLObject;
import main.java.com.typhon.evolutiontool.utils.SMOFactory;
import main.java.com.typhon.evolutiontool.utils.TyphonMLUtils;
import typhonml.ChangeOperator;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ChangeOperatorsTests extends InitialTest {

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
}
