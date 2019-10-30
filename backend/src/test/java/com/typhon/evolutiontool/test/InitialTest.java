package com.typhon.evolutiontool.test;

import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Before;
import typhonml.Model;

public class InitialTest {

    EvolutionServiceImpl evolutionService = new EvolutionServiceImpl();
    static Model sourceModel, targetModel;

    @Before
    public void setUp() {
        TyphonMLUtils.typhonMLPackageRegistering();
    }
}
