package com.typhon.evolutiontool.services;


import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.TyphonMLObject;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import typhonml.Model;

import java.util.List;


@Component
public class EvolutionToolFacadeImpl implements EvolutionToolFacade{

    Logger logger = LoggerFactory.getLogger(EvolutionToolFacadeImpl.class);
    private EvolutionService evolutionService;
    private Model model;

    @Autowired
    public EvolutionToolFacadeImpl(EvolutionService evolutionService) {
        this.evolutionService = evolutionService;
    }

    public EvolutionToolFacadeImpl() {

    }

    public Model executeChangeOperators(Model model) throws InputParameterException {
        List<SMO> smoList;
        logger.info("Received TyphonML model : [" + model + "]");
        smoList = TyphonMLUtils.getListSMOFromChangeOperators(model);
        for (SMO smo : smoList) {
            logger.info("Processing SMO : [" + smo + "]");
            if(smo.getTyphonObject()==TyphonMLObject.ENTITY){
                if (smo.getEvolutionOperator() == EvolutionOperator.ADD)
                    model = evolutionService.addEntityType(smo,model);
                if (smo.getEvolutionOperator()==EvolutionOperator.REMOVE)
                    model = evolutionService.removeEntityType(smo, model);
                if(smo.getEvolutionOperator()==EvolutionOperator.RENAME)
                    model = evolutionService.renameEntityType(smo,model);
                if(smo.getEvolutionOperator()==EvolutionOperator.MIGRATE)
                    model = evolutionService.migrateEntity(smo,model);
                if (smo.getEvolutionOperator()==EvolutionOperator.SPLITHORIZONTAL)
                    model = evolutionService.splitHorizontal(smo, model);
                if(smo.getEvolutionOperator()==EvolutionOperator.SPLITVERTICAL)
                    model = evolutionService.splitVertical(smo, model);
                if (smo.getEvolutionOperator() == EvolutionOperator.MERGE) {
                    //TODO
                }
            }
            if (smo.getTyphonObject() == TyphonMLObject.RELATION) {
                if (smo.getEvolutionOperator() == EvolutionOperator.ADD)
                    model = evolutionService.addRelationship(smo,model);
                if (smo.getEvolutionOperator()==EvolutionOperator.REMOVE)
                    model = evolutionService.removeRelationship(smo, model);
                if(smo.getEvolutionOperator()==EvolutionOperator.RENAME){}
                //TODO
                if(smo.getEvolutionOperator()==EvolutionOperator.ENABLECONTAINMENT)
                    model = evolutionService.enableContainmentInRelationship(smo, model);
                if(smo.getEvolutionOperator()==EvolutionOperator.DISABLECONTAINMENT)
                    model = evolutionService.disableContainmentInRelationship(smo, model);
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

