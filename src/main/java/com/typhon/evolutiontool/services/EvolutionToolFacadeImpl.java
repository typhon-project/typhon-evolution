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


@Component
public class EvolutionToolFacadeImpl implements EvolutionToolFacade{

    Logger logger = LoggerFactory.getLogger(EvolutionToolFacadeImpl.class);
    private EvolutionService evolutionService;

    @Autowired
    public EvolutionToolFacadeImpl(EvolutionService evolutionService) {
        this.evolutionService = evolutionService;
    }

    public String executeSMO(SMO smo) throws InputParameterException {
        logger.info("Received SMO : [" + smo + "]");
        logger.info("Registering TyphonML Package needed resources ");
        TyphonMLUtils.typhonMLPackageRegistering();
//        if(smo.getTyphonObject()==TyphonMLObject.ENTITY && smo.getEvolutionOperator()== EvolutionOperator.ADD)
//            return evolutionService.addEntityType(smo);
//        if(smo.getTyphonObject()==TyphonMLObject.ENTITY && smo.getEvolutionOperator()== EvolutionOperator.RENAME)
//            return evolutionService.renameEntityType(smo);
//        if (smo.getTyphonObject() == TyphonMLObject.ENTITY && smo.getEvolutionOperator() == EvolutionOperator.MIGRATE) {
//            return evolutionService.migrateEntity(smo);
//        }
        return "";

//        return null;
    }


}

