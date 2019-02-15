package com.typhon.evolutiontool;

import com.typhon.evolutiontool.controller.RestController;
import com.typhon.evolutiontool.services.EvolutionToolFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(RestController.class)
public class RestControllerTest {

    private final String ADD_ENTITY_JSON = "{"+
        "\"smo\":{"+
            "\"typhonObject\":\"Entity\","+
                    "\"evolutionOperator\":\"Add\","+
                    "\"input\":{"+
                "\"entity\":\"Professor\","+
                        "\"attributes\":{"+
                    "\"name\":\"string\","+
                            "\"hireDate\":\"date\""+
                "},"+
                "\"databasetype\":\"relationaldb\","+
                        "\"databasemappingname\":\"Professor\","+
                        "\"id\":\"name\""+
            "}"
        +"}"
    +"}";

    @Autowired
    private MockMvc mvc;

    @Test
    public void testSendSMO() throws Exception {
        mvc.perform(post("/smo").contentType(MediaType.APPLICATION_JSON).content(ADD_ENTITY_JSON)).andReturn().getResponse();
    }

}
