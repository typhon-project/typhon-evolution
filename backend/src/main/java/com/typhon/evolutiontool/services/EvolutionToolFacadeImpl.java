package com.typhon.evolutiontool.services;


import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.TyphonMLObject;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Model;

import java.util.List;

public class EvolutionToolFacadeImpl implements EvolutionToolFacade{

    Logger logger = LoggerFactory.getLogger(EvolutionToolFacadeImpl.class);
    private EvolutionService evolutionService;
    private Model model;

    public EvolutionToolFacadeImpl(EvolutionService evolutionService) {
        this.evolutionService = evolutionService;
    }

    public Model executeChangeOperators(Model model) throws InputParameterException, EvolutionOperationNotSupported {
        List<SMO> smoList;

        logger.info("Received TyphonML model : [" + model + "]");
        smoList = TyphonMLUtils.getListSMOFromChangeOperators(model);
        for (SMO smo : smoList) {

            logger.info("Processing SMO : [" + smo + "]");
            if(smo.getTyphonObject()==TyphonMLObject.ENTITY){
                model = evolutionService.evolveEntity(smo, model);
            }
            if (smo.getTyphonObject() == TyphonMLObject.RELATION) {
                model = evolutionService.evolveRelation(smo, model);
            }
            //...

            //TODO : Saving the ChangeOperator or SMO to a Database

        }
        //Removing Change Operators from the model
        //TODO : Change to only remove executed ones?
        TyphonMLUtils.removeChangeOperators(model);

        return model;
    }

    public void setEvolutionService(EvolutionService evolutionService) {
        this.evolutionService = evolutionService;
    }
}

