package com.typhon.evolutiontool.services;


import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.TyphonMLObject;
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

    public String executeSMO(SMO smo) {
        logger.info("Received SMO : [" + smo + "]");
        if(smo.getTyphonObject()==TyphonMLObject.ENTITY && smo.getEvolutionOperator()==EvolutionOperator.ADD)
            return evolutionService.addEntity(smo);
        if(smo.getTyphonObject()==TyphonMLObject.ENTITY && smo.getEvolutionOperator()==EvolutionOperator.RENAME)
            return evolutionService.renameEntity(smo);

        return null;
    }


}

