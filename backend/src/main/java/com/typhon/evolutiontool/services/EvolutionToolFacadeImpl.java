package com.typhon.evolutiontool.services;


import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.TyphonMLObject;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class EvolutionToolFacadeImpl implements EvolutionToolFacade {

    private Logger logger = LoggerFactory.getLogger(EvolutionToolFacadeImpl.class);
    private EvolutionService evolutionService = new EvolutionServiceImpl();

    public Model executeChangeOperators(Model model) throws InputParameterException, EvolutionOperationNotSupported {
        List<SMO> smoList;

        logger.info("Received TyphonML model : [" + model + "]");
        smoList = TyphonMLUtils.getListSMOFromChangeOperators(model);
        for (SMO smo : smoList) {

            logger.info("Processing SMO : [" + smo + "]");
            if (smo.getTyphonObject() == TyphonMLObject.ENTITY) {
                model = evolutionService.evolveEntity(smo, model);
            }
            if (smo.getTyphonObject() == TyphonMLObject.RELATION) {
                model = evolutionService.evolveRelation(smo, model);
            }
            if (smo.getTyphonObject() == TyphonMLObject.ATTRIBUTE) {
                model = evolutionService.evolveAttribute(smo, model);
            }
            if (smo.getTyphonObject() == TyphonMLObject.INDEX) {
                model = evolutionService.evolveIndex(smo, model);
            }
            //...

            //TODO : Saving the ChangeOperator or SMO to a Database

        }
        //Removing Change Operators from the model
        //TODO : Change to only remove executed ones?
        TyphonMLUtils.removeChangeOperators(model);

        return model;
    }

    @Override
    public Model executeChangeOperators(String changeOperatorsFilePath) {
        Model modelToEvolve = null, newModel = null;
        String message = "Evolution operators have been successfully executed on the model";
        try {
            modelToEvolve = evolutionService.prepareEvolution(changeOperatorsFilePath);
        } catch (Exception e) {
            message = e.getMessage();
            logger.error(message);
        }
        try {
            newModel = executeChangeOperators(modelToEvolve);
        } catch (Exception e) {
            message = "Error while evolving the model";
            logger.error(message);
        }
        logger.info(message);
        //Log the result message in the input file
        try {
            Files.write(Paths.get(changeOperatorsFilePath), (System.lineSeparator() + message).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("Error while writing the evolution message to the input file. Result message content: " + message);
        }
        return newModel;
    }

    @Override
    public String evolve(String initialModelPath, String finalModelPath) {
        Model model;

        logger.info("Registering TyphonML Package needed resources ");
        TyphonMLUtils.typhonMLPackageRegistering();
        model = TyphonMLUtils.loadModelTyphonML(initialModelPath);
        if (model == null) {
            logger.error("FAILED to load initial model [{}]", initialModelPath);
            return ("FAILED to load initial model");
        }

        try {
//	            evolutionToolFacade.executeSMO(Arrays.asList(smo),"resources/baseModel.xmi","resources/baseModel.xmi");
            model = this.executeChangeOperators(model);
            // Saving last model
            logger.info("Saving last TyphonML model to  [" + finalModelPath + "]");
            TyphonMLUtils.saveModel(model, finalModelPath);
            return "Change Operators inside TyphonML model in [" + initialModelPath + "] executed and new TyphonML model saved in [" + finalModelPath + "]";
        } catch (InputParameterException exception) {
            logger.error("Missing input parameters");
            return ("FAILED " + exception.getMessage());
        } catch (EvolutionOperationNotSupported exception) {
            logger.error("The operation required is not yet supported");
            return ("FAILED " + exception.getMessage());
        }


    }
}

