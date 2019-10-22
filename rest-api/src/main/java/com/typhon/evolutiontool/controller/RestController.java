package com.typhon.evolutiontool.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.typhon.evolutiontool.Message;

import main.java.com.typhon.evolutiontool.services.EvolutionToolFacade;


public class RestController {

    Logger logger = LoggerFactory.getLogger(RestController.class);
    private EvolutionToolFacade evolutionToolFacade;


    @Autowired
    private RestController(EvolutionToolFacade evolutionToolFacade) {
        this.evolutionToolFacade = evolutionToolFacade;
    }

    @RequestMapping(value="/evolve", method=RequestMethod.POST)
    @ResponseBody
    public Message postSmo(@RequestParam("initialModelPath") String initialModelPath, @RequestParam("finalModelPath") String finalModelPath){
//        SMOJsonImpl smo = modelMapper.map(smoDto, SMOJsonImpl.class);
        Message message = new Message("test");
       // TODO Call backend.EvolutionToolFacade . evolve 
        
        
        return message;
    }

}
