package com.typhon.evolutiontool.services;

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
}
