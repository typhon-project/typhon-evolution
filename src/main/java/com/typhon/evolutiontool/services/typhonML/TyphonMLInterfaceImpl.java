package com.typhon.evolutiontool.services.typhonML;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
public class TyphonMLInterfaceImpl implements TyphonMLInterface {

    Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);

    @Override
    public void setNewTyphonMLModel(String newModelIdentifier) {
        logger.info("Setting current TyphonML to [{}] ", newModelIdentifier);
        //TODO Implement TyphonML interface
    }

    @Override
    public Entity getEntityTypeFromId(String entityid) {
        logger.info("Getting Entity type object from Id [{}]", entityid);
        //TODO Implement real retrieving + casting to Entity object
        return null;
    }
}
