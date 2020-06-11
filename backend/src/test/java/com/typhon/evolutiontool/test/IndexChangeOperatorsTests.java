package com.typhon.evolutiontool.test;

import com.typhon.evolutiontool.entities.SMOAdapter;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.SMOFactory;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Assert;
import org.junit.Test;
import typhonml.*;

public class IndexChangeOperatorsTests extends InitialTest {

    @Test
    public void testAddTableIndex() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/addTableIndexChangeOperator.xmi");
        AddIndex addIndex = (AddIndex) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addIndex);

        targetModel = evolutionService.evolveIndex(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/addTableIndexChangeOperator_final.xmi");
    }

    @Test
    public void testAddCollectionIndex() throws InputParameterException, EvolutionOperationNotSupported {
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/addCollectionIndexChangeOperator.xmi");
        AddCollectionIndex addCollectionIndex = (AddCollectionIndex) sourceModel.getChangeOperators().get(0);
        SMOAdapter smo = SMOFactory.createSMOAdapterFromChangeOperator(addCollectionIndex);

        targetModel = evolutionService.evolveIndex(smo, sourceModel);
        TyphonMLUtils.removeChangeOperators(targetModel);
        TyphonMLUtils.saveModel(targetModel, "src/test/resources/addCollectionIndexChangeOperator_final.xmi");
    }
}
