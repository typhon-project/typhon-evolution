package com.typhon.evolutiontool.controller;

import com.typhon.evolutiontool.Message;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.SMODto;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionToolFacade;
import com.typhon.evolutiontool.services.EvolutionToolFacadeImpl;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;


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
    public Message postSmo(@RequestBody SMODto smoDto){
        SMO smo = modelMapper.map(smoDto, SMO.class);
        Message message;
        try {
            //TODO Change how to pass source and target typhonML together with SMO List
            evolutionToolFacade.executeSMO(Arrays.asList(smo),"resources/baseModel.xmi","resources/baseModel.xmi");
            message = new Message("[" + smo.toString() + "] executed");
        } catch (InputParameterException exception) {
            logger.error("Missing input parameters");
            message = new Message("FAILED "+exception.getMessage());
        }
        return message;
    }

}
