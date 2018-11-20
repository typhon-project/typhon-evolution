package com.typhon.evolutiontool.services;


import com.typhon.evolutiontool.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.TyphonMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("firstImpl")
public class EvolutionToolFacadeImpl implements EvolutionToolFacade{


    Logger logger = LoggerFactory.getLogger(EvolutionToolFacadeImpl.class);

    @Autowired
    private MessageRepository messageRepository;

    public Message executeSMO(SMO smo, long counter) {
        Message m = new Message(smo.toString(), counter);
        logger.info("Executing SMO [" + smo +"]");
        messageRepository.save(m);
        return m;
    }

    @Override
    public boolean verifyInputParameter(SMO smo) {
        if (smo.getTyphonObject() == TyphonMLObject.ENTITY) {
            if (smo.getEvolutionOperator() == EvolutionOperator.ADD) {
                getEntityParameter(smo.getInputParameter());
                return true;
            }
        }
        return false;
    }

    @Override
    public StructureChange createStructureChanges(SMO smo) {
        StructureChange structureChange = new StructureChange();
        structureChange = structureChange.computeChanges(smo);
        return structureChange;
    }


    private Entity getEntityParameter(JsonNode inputParameter) {
        //Convert to Entity if possible
        ObjectMapper mapper = new ObjectMapper();
        Entity e ;
        try {
            e = mapper.treeToValue(inputParameter, Entity.class);
            logger.info("Entity extracted : ["+e+"]");
            return e;
        } catch (IOException i) {
            return null;
        }
    }

}

