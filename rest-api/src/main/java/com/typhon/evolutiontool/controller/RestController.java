package com.typhon.evolutiontool.controller;

import com.typhon.evolutiontool.Message;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionToolFacade;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import typhonml.Model;


@org.springframework.web.bind.annotation.RestController
public class RestController {

    Logger logger = LoggerFactory.getLogger(RestController.class);
    private EvolutionToolFacade evolutionToolFacade;
    private ModelMapper modelMapper = new ModelMapper();


    @Autowired
    private RestController(EvolutionToolFacade evolutionToolFacade) {
        this.evolutionToolFacade = evolutionToolFacade;
    }

    @RequestMapping(value="/evolve", method=RequestMethod.POST)
    @ResponseBody
    public Message postSmo(@RequestParam("initialModelPath") String initialModelPath, @RequestParam("finalModelPath") String finalModelPath){
//        SMOJsonImpl smo = modelMapper.map(smoDto, SMOJsonImpl.class);
        Message message;
       // TODO Call backend.EvolutionToolFacade . evolve 
        
        
        return message;
    }

}
