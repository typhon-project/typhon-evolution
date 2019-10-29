package main.java.com.typhon.evolutiontool.handlers.entity;

import main.java.com.typhon.evolutiontool.entities.ParametersKeyString;
import main.java.com.typhon.evolutiontool.entities.SMO;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import main.java.com.typhon.evolutiontool.handlers.BaseHandler;
import main.java.com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import main.java.com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import typhonml.Model;

import java.util.Collections;

public class EntityRemoveHandler extends BaseHandler {

    public EntityRemoveHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        Model targetModel;
        String entityname;

        if (containParameters(smo, Collections.singletonList(ParametersKeyString.ENTITYNAME))) {
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            //If the entity is involved in a relationship. Abort
            if (typhonMLInterface.hasRelationship(entityname, model)) {
                throw new InputParameterException("Cannot delete an entity involved in a relationship. Remove the relationships first.");
            }
            //Delete data
            typhonQLInterface.deleteAllEntityData(entityname, model);
            //Delete structures
            typhonQLInterface.deleteEntityStructure(entityname, model);

            targetModel = typhonMLInterface.deleteEntityMappings(entityname, model);
            targetModel = typhonMLInterface.deleteEntityType(entityname, targetModel);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.ENTITYNAME + "]");
        }
    }

}
