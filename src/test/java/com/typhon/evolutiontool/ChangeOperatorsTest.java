package com.typhon.evolutiontool;

import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.SMOAdapter;
import com.typhon.evolutiontool.entities.TyphonMLObject;
import com.typhon.evolutiontool.services.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Before;
import org.junit.Test;
import typhonml.ChangeOperator;
import typhonml.Model;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ChangeOperatorsTest {

    public static final String sourcemodelpath = "resources/complexModelWithChangeOperators.xmi";
    public Model sourceModel;

    @Before
    public void setUp() {
        TyphonMLUtils.typhonMLPackageRegistering();
        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
    }

    @Test
    public void testReadChangeOperators(){
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

}
