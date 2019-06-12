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

    public String executeSMO(List<SMO> smoList, String initialModelPath, String finalModelPath) throws InputParameterException {
        logger.info("Received list SMO : [" + smoList + "]");
        logger.info("Registering TyphonML Package needed resources ");
        TyphonMLUtils.typhonMLPackageRegistering();
        //Get initial model. To adapt TODO
        model = TyphonMLUtils.loadModelTyphonML(initialModelPath);
        if (model == null) {
            throw new InputParameterException("Could not load initial model");
        }
        for (SMO smo : smoList ) {
            logger.info("Processing SMO : [" + smo + "]");
            if(smo.getTyphonObject()==TyphonMLObject.ENTITY && smo.getEvolutionOperator()== EvolutionOperator.ADD)
                model = evolutionService.addEntityType(smo,model);
            if (smo.getTyphonObject() == TyphonMLObject.ENTITY && smo.getEvolutionOperator() == EvolutionOperator.SPLITHORIZONTAL) {
                model = evolutionService.splitHorizontal(smo, model);
            }
            if(smo.getTyphonObject()==TyphonMLObject.ENTITY && smo.getEvolutionOperator()== EvolutionOperator.RENAME)
//                model = evolutionService.renameEntityType(smo,model);
            if (smo.getTyphonObject() == TyphonMLObject.ENTITY && smo.getEvolutionOperator() == EvolutionOperator.MIGRATE) {
//                model = evolutionService.migrateEntity(smo,model);
            }
        }
        // Saving last model
        logger.info("Saving last TyphonML model to  [" + finalModelPath + "]");
        TyphonMLUtils.saveModel(model,finalModelPath);

        return "List of SMO processed";
    }

    public void setEvolutionService(EvolutionService evolutionService) {
        this.evolutionService = evolutionService;
    }
}

