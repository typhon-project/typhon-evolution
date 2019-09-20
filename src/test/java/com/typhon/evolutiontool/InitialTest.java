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

    protected TyphonDLInterface typhonDLInterface = new TyphonDLInterfaceImpl();
    protected TyphonQLInterface typhonQLInterface = new TyphonInterfaceQLImpl();
    protected TyphonMLInterface typhonMLInterface = new TyphonMLInterfaceImpl();
    protected EvolutionServiceImpl evolutionService = new EvolutionServiceImpl(typhonQLInterface, typhonMLInterface, typhonDLInterface);
    protected ObjectMapper mapper = new ObjectMapper();
    protected SMO smo;
    protected static Model sourceModel, targetModel;
    //    protected static final String sourcemodelpath = "resources/baseModel.xmi";
    protected static final String sourcemodelpath = "resources/generated_demo.xmi";
    protected static final String finalModelPath = "resources/finalModel.xmi";

    @Before
    public void setUp() {
        TyphonMLUtils.typhonMLPackageRegistering();
        evolutionService.setTyphonDLInterface(typhonDLInterface);
        evolutionService.setTyphonQLInterface(typhonQLInterface);
        evolutionService.setTyphonMLInterface(typhonMLInterface);
    }
}
