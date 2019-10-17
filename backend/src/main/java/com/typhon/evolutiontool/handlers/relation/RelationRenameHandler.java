package main.java.com.typhon.evolutiontool.handlers.relation;

import main.java.com.typhon.evolutiontool.entities.ParametersKeyString;
import main.java.com.typhon.evolutiontool.entities.SMO;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import main.java.com.typhon.evolutiontool.handlers.BaseHandler;
import main.java.com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import main.java.com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import typhonml.Entity;
import typhonml.Model;
import typhonml.Relation;

import java.util.Arrays;


public class RelationRenameHandler extends BaseHandler {

    public RelationRenameHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION, ParametersKeyString.RELATIONNAME))) {
            Relation relation = (Relation) smo.getInputParameter().get(ParametersKeyString.RELATION);
            String newRelationName = smo.getInputParameter().get(ParametersKeyString.RELATIONNAME).toString();
            Model targetModel = typhonMLInterface.renameRelation(relation.getName(), ((Entity) relation.eContainer()).getName(), newRelationName, model);
            typhonQLInterface.renameRelation(relation.getName(), newRelationName, targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.RELATION + ", " + ParametersKeyString.RELATIONNAME + "]");
        }
    }

}
