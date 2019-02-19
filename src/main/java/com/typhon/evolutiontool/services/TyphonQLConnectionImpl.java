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
    public TyphonQLConnection getTyphonQLConnectionOnSpecifiedTyphonML(String typhonMLversion) {
        logger.info("Connection TyphonQL to TyphonML [{}]", typhonMLversion);
        //TODO implement effective connection
        return this;
    }

    @Override
    public String executeTyphonQLDDL(String tqlDDL) {
        logger.info("Executing TyphonQL DDL [{}]", tqlDDL);
        //TODO implement effective execution
        return tqlDDL;
    }
}
