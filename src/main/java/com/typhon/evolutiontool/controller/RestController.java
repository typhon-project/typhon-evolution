package com.typhon.evolutiontool.controller;

import com.typhon.evolutiontool.Message;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.services.EvolutionToolFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.atomic.AtomicLong;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private EvolutionToolFacade evolutionToolFacade;

    @Autowired
    private RestController(@Qualifier("firstImpl") EvolutionToolFacade evolutionToolFacade) {
        this.evolutionToolFacade = evolutionToolFacade;
    }

    @RequestMapping("/message")
    public Message returnMessage(@RequestParam(value="content",defaultValue = "No content") String content) {
        return new Message(String.format(template, content), counter.incrementAndGet());
    }

    @RequestMapping(value="/smo", method=RequestMethod.POST)
    public Message postSmo(@RequestBody SMO smo){
        // Verify parameters
        if (!evolutionToolFacade.verifyInputParameter(smo)) {
            return new Message("The input JSON parameter string does not fit the required input string for the specified Schema modification operator");
        } else
            //execute if correct parameter.
            return evolutionToolFacade.executeSMO(smo, counter.incrementAndGet());
    }

}
