package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonQL.TyphonInterfaceQLImpl;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Before;
import typhonml.Model;

public class InitialTest {

    static final String SOURCE_MODEL_PATH = "resources/generated_demo.xmi";
    static final String FINAL_MODEL_PATH = "resources/finalModel.xmi";

    protected TyphonDLInterface typhonDLInterface = new TyphonDLInterfaceImpl();
    protected TyphonQLInterface typhonQLInterface = new TyphonInterfaceQLImpl();
    protected TyphonMLInterface typhonMLInterface = new TyphonMLInterfaceImpl();
    protected EvolutionServiceImpl evolutionService = new EvolutionServiceImpl(typhonQLInterface, typhonMLInterface, typhonDLInterface);

    ObjectMapper mapper = new ObjectMapper();
    SMO smo;
    protected static Model sourceModel, targetModel;

    @Before
    public void setUp() {
        TyphonMLUtils.typhonMLPackageRegistering();
        evolutionService.setTyphonDLInterface(typhonDLInterface);
        evolutionService.setTyphonQLInterface(typhonQLInterface);
        evolutionService.setTyphonMLInterface(typhonMLInterface);
    }
}
