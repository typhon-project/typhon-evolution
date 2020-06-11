package com.typhon.evolutiontool.handlers;

import com.typhon.evolutiontool.datatypes.DataTypeDO;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.DataTypeDOFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseHandler implements Handler {
    protected Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);

    protected TyphonDLInterface typhonDLInterface;
    protected TyphonMLInterface typhonMLInterface;
    protected TyphonQLInterface typhonQLInterface;

    public BaseHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        typhonMLInterface = tml;
        typhonQLInterface = tql;
        typhonDLInterface = tdl;
    }


    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        return null;
    }

    protected boolean containParameters(SMO smo, List<ChangeOperatorParameter> parameters) {
        logger.info("Verifying input parameter for [{}] - [{}] operator", smo.getTyphonObject(), smo.getEvolutionOperator());
        return smo.inputParametersContainsExpected(parameters);
    }

    protected DatabaseType getDatabaseType(Database database) {
        if (database != null) {
            if (database instanceof RelationalDB) {
                return DatabaseType.RELATIONALDB;
            }
            if (database instanceof DocumentDB) {
                return DatabaseType.DOCUMENTDB;
            }
            if (database instanceof ColumnDB) {
                return DatabaseType.COLUMNDB;
            }
            if (database instanceof GraphDB) {
                return DatabaseType.GRAPHDB;
            }
            if (database instanceof KeyValueDB) {
                return DatabaseType.KEYVALUE;
            }
        }
        return null;
    }

    protected void checkEntityRelations(String sourceEntityName, EntityDO entityDO) {
        if (entityDO != null && entityDO.getRelations() != null) {
            for (RelationDO relationDO : entityDO.getRelations()) {
                if (relationDO.getTypeName().equals(sourceEntityName)) {
                    relationDO.setTypeName(entityDO.getName());
                }
            }
        }
    }

    protected Map<String, DataTypeDO> buildEntityAttributes(List<Attribute> attributes) {
        Map<String, DataTypeDO> entityAttributes = new HashMap<>();
        attributes.forEach(attribute -> entityAttributes.put(attribute.getName(), DataTypeDOFactory.buildInstance(attribute.getType())));
        return entityAttributes;
    }
}
