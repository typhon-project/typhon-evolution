package com.typhon.evolutiontool;

import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionToolFacade;
import com.typhon.evolutiontool.services.EvolutionToolFacadeImpl;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import typhonml.Model;

public class EvolutionTool {

    private EvolutionToolFacade evolutionToolFacade = new EvolutionToolFacadeImpl();

    public String evolve(String initialModelPath, String finalModelPath) {
        TyphonMLUtils.typhonMLPackageRegistering();
        String message;
        Model model = TyphonMLUtils.loadModelTyphonML(initialModelPath);
        if (model == null) {
            return "FAILED to load initial model";
        }
        try {
            model = evolutionToolFacade.executeChangeOperators(model);
            message = "Change Operators inside TyphonML model in [" + initialModelPath + "] executed and new TyphonML model saved in [" + finalModelPath + "]";
        } catch (InputParameterException | EvolutionOperationNotSupported exception) {
            message = "FAILED " + exception.getMessage();
        }

        TyphonMLUtils.saveModel(model, finalModelPath);

        return message;
    }

}
