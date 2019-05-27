package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionService;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import com.typhon.evolutiontool.services.EvolutionToolFacadeImpl;
import com.typhon.evolutiontool.services.TyphonInterface;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonQL.TyphonInterfaceQLImpl;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EvolutionToolFacadeTest {


    private ObjectMapper mapper = new ObjectMapper();
    private SMO smo;
    private TyphonDLInterface typhonDLInterface = new TyphonDLInterfaceImpl();
    private TyphonInterface typhonInterface = new TyphonInterfaceQLImpl();
    private TyphonMLInterface typhonMLInterface = new TyphonMLInterfaceImpl();
    private EvolutionServiceImpl evolutionService = new EvolutionServiceImpl();
    private EvolutionToolFacadeImpl evolutionToolFacade = new EvolutionToolFacadeImpl();
    private final String sourcePath = "resources/emptyModel.xmi";
    private final String targetPath = "resources/finalModel.xmi";


    @Before
    public void setUp() {
        TyphonMLUtils.typhonMLPackageRegistering();
        evolutionService.setTyphonDLInterface(typhonDLInterface);
        evolutionService.setTyphonInterface(typhonInterface);
        evolutionService.setTyphonMLInterface(typhonMLInterface);
        evolutionToolFacade.setEvolutionService(evolutionService);
    }

    @Test
    public void testProcessListSMOs() throws IOException, InputParameterException {
        SMO smo1, smo2;
        smo1 = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid.json"));
        smo2 = mapper.readerFor(SMO.class).readValue(new File("src/main/resources/test/CreateEntitySmoValid2.json"));
        List<SMO> listSmo = new ArrayList<>();
        listSmo.add(smo1);
        listSmo.add(smo2);
        evolutionToolFacade.executeSMO(listSmo, sourcePath, targetPath);
    }


}
