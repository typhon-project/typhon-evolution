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
        Model model;
        logger.info("Registering TyphonML Package needed resources ");
        TyphonMLUtils.typhonMLPackageRegistering();
        model = TyphonMLUtils.loadModelTyphonML(initialModelPath);
        if (model == null) {
            logger.error("FAILED to load initial model [{}]",initialModelPath);
            message = new Message("FAILED to load initial model");
        }

        try {
//            evolutionToolFacade.executeSMO(Arrays.asList(smo),"resources/baseModel.xmi","resources/baseModel.xmi");
            model= evolutionToolFacade.executeChangeOperators(model);
            message = new Message("Change Operators inside TyphonML model in ["+initialModelPath+"] executed and new TyphonML model saved in ["+finalModelPath+"]");
        } catch (InputParameterException exception) {
            logger.error("Missing input parameters");
            message = new Message("FAILED "+exception.getMessage());
        } catch (EvolutionOperationNotSupported exception){
            logger.error("The operation required is not yet supported");
            message = new Message("FAILED "+exception.getMessage());
        }

        // Saving last model
        logger.info("Saving last TyphonML model to  [" + finalModelPath + "]");
        TyphonMLUtils.saveModel(model,finalModelPath);

        return message;
    }

}
