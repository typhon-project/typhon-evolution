package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TyphonQLGeneratorImpl implements TyphonQLGenerator {

    Logger logger = LoggerFactory.getLogger(TyphonQLGeneratorImpl.class);

    @Override
    public String createEntity(Entity newEntity) {
        String tql;
        tql="TyphonQL CREATE ENTITY "+newEntity.getEntityName()+" {"+newEntity.getAttributes().entrySet().stream().map(entry -> entry.getKey()+" "+entry.getValue()).collect(Collectors.joining(","))+"}";
        logger.info(tql);
        return tql;
    }

}
