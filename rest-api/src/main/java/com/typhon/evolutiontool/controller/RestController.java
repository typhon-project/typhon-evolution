package com.typhon.evolutiontool.controller;

import com.typhon.evolutiontool.Message;
import com.typhon.evolutiontool.services.EvolutionToolFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


public class RestController {

    private EvolutionToolFacade evolutionToolFacade;

    @Autowired
    private RestController(EvolutionToolFacade evolutionToolFacade) {
        this.evolutionToolFacade = evolutionToolFacade;
    }

    @RequestMapping(value = "/evolve", method = RequestMethod.POST)
    @ResponseBody
    public Message postSmo(@RequestParam("initialModelPath") String initialModelPath, @RequestParam("finalModelPath") String finalModelPath) {
        String result = evolutionToolFacade.evolve(initialModelPath, finalModelPath);

        return new Message(result);
    }

}
