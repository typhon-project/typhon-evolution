package test;

import main.java.com.typhon.evolutiontool.entities.SMO;
import main.java.com.typhon.evolutiontool.services.EvolutionServiceImpl;
import main.java.com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import main.java.com.typhon.evolutiontool.services.typhonDL.TyphonDLInterfaceImpl;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterfaceImpl;
import main.java.com.typhon.evolutiontool.services.typhonQL.TyphonInterfaceQLImpl;
import main.java.com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import main.java.com.typhon.evolutiontool.utils.TyphonMLUtils;

import org.junit.Before;
import typhonml.Model;

public class InitialTest {

    static final String SOURCE_MODEL_PATH = "resources/generated_demo.xmi";
    static final String FINAL_MODEL_PATH = "resources/finalModel.xmi";

    protected TyphonDLInterface typhonDLInterface = new TyphonDLInterfaceImpl();
    protected TyphonQLInterface typhonQLInterface = new TyphonInterfaceQLImpl();
    protected TyphonMLInterface typhonMLInterface = new TyphonMLInterfaceImpl();
    protected EvolutionServiceImpl evolutionService = new EvolutionServiceImpl(typhonQLInterface, typhonMLInterface, typhonDLInterface);

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
