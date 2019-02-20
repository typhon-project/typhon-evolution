package com.typhon.evolutiontool.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TyphonQLConnectionImpl implements TyphonQLConnection {

    Logger logger = LoggerFactory.getLogger(TyphonQLConnectionImpl.class);


    public TyphonQLConnectionImpl() {
    }

    @Override
    public String executeTyphonQLDDL(String tqlDDL, String typhonMLversion) {
        logger.info("Executing TyphonQL DDL [{}] \n on TyphonML [{}]", tqlDDL, typhonMLversion);
        //TODO implement effective execution
        return tqlDDL;
    }
}
