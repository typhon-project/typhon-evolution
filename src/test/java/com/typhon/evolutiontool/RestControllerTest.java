package com.typhon.evolutiontool;

import com.typhon.evolutiontool.controller.RestController;
import com.typhon.evolutiontool.services.EvolutionToolFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(RestController.class)
public class RestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EvolutionToolFacade evolutionToolFacade;

    @Test
    public void testSendSMO() {

    }

}
