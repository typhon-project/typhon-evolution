package com.typhon.evolutiontool.test;

import com.typhon.evolutiontool.EvolutionTool;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Test;

public class ChangeOperatorsTests extends InitialTest {

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
