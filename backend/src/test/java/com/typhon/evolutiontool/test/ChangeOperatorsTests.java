package com.typhon.evolutiontool.test;

import com.typhon.evolutiontool.EvolutionTool;
import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMOAdapter;
import com.typhon.evolutiontool.entities.TyphonMLObject;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Assert;
import org.junit.Test;
import typhonml.ChangeOperator;

import java.util.List;

public class ChangeOperatorsTests extends InitialTest {

    @Test
    public void testReadChangeOperators() {
        sourceModel = TyphonMLUtils.loadModelTyphonML("resources/complexModelWithChangeOperators.xmi");
        List<ChangeOperator> changeOperatorList = sourceModel.getChangeOperators();
        ChangeOperator changeOperator;
        changeOperator = changeOperatorList.get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(changeOperator);
        Assert.assertEquals(TyphonMLObject.ENTITY, smo.getTyphonObject());
        Assert.assertEquals(EvolutionOperator.REMOVE, smo.getEvolutionOperator());

        changeOperator = changeOperatorList.get(1);
        SMOAdapter smo2 = SMOFactory.createSMOAdapterFromChangeOperator(changeOperator);
        Assert.assertEquals(TyphonMLObject.ENTITY, smo2.getTyphonObject());
        Assert.assertEquals(EvolutionOperator.RENAME, smo2.getEvolutionOperator());
    }

    @Test
    public void testMultipleOperators() {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/multi-op.xmi");
        EvolutionTool evolutionTool = new EvolutionTool();
        evolutionTool.evolve("src/test/resources/multi-op.xmi", "src/test/resources/multi-op-final.xmi");
    }

    @Test
    public void testMultipleOperators2() {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/multi-op-final.xmi");
        EvolutionTool evolutionTool = new EvolutionTool();
        evolutionTool.evolve("src/test/resources/multi-op-final.xmi", "src/test/resources/multi-op-final.xmi");
    }
}
