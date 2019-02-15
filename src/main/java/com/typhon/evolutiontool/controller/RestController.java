package com.typhon.evolutiontool.controller;

import com.typhon.evolutiontool.Message;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.SMODto;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionToolFacade;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@org.springframework.web.bind.annotation.RestController
public class RestController {


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
            evolutionToolFacade.executeSMO(smo);
            message = new Message("[" + smo.toString() + "] executed");
        } catch (InputParameterException exception) {
            message = new Message("FAILED "+exception.getMessage());
        }
        return message;
    }

}
