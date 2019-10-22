package main.java.com.typhon.evolutiontool.handlers.entity;

import java.util.Arrays;

import main.java.com.typhon.evolutiontool.entities.CardinalityDO;
import main.java.com.typhon.evolutiontool.entities.EntityDO;
import main.java.com.typhon.evolutiontool.entities.ParametersKeyString;
import main.java.com.typhon.evolutiontool.entities.RelationDO;
import main.java.com.typhon.evolutiontool.entities.RelationDOImpl;
import main.java.com.typhon.evolutiontool.entities.SMO;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import main.java.com.typhon.evolutiontool.handlers.BaseHandler;
import main.java.com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import main.java.com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import main.java.com.typhon.evolutiontool.utils.EntityDOFactory;
import typhonml.Entity;
import typhonml.Model;

public class EntitySplitVerticalHandler extends BaseHandler {

    public EntitySplitVerticalHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    /**
     * Partially migrates the instances of sourceEntity to a new entity targetEntity. Only the values
     * of attributes [attributesNames] are migrated. The link between the instances of entity1 and entity2 is
     * kept via a new one-to-one relationship relName.
     *
     * @param smo the change operator to apply
     * @return the updated model, after the change operator has been applied
     * @throws InputParameterException when a mandatory parameter is missing
     */
    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        String databasetype, databasename;
//        String sourceEntityId;
        RelationDO relation;
        EntityDO sourceEntity, firstNewEntity, secondNewEntity;
//        WorkingSet dataSource, dataTarget;
        Model targetModel;

//        dataTarget = WorkingSetFactory.createEmptyWorkingSet();


        if (containParameters(smo, Arrays.asList(
                ParametersKeyString.ENTITY,
                ParametersKeyString.FIRSTNEWENTITY,
                ParametersKeyString.SECONDNEWENTITY,
                ParametersKeyString.DATABASENAME,
                ParametersKeyString.DATABASETYPE))) {
            sourceEntity = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ParametersKeyString.ENTITY));
            firstNewEntity = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ParametersKeyString.FIRSTNEWENTITY));
            secondNewEntity = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ParametersKeyString.SECONDNEWENTITY));
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();

            //TyphonDL
            if (!typhonDLInterface.isDatabaseRunning(databasetype, databasename)) {
                typhonDLInterface.createDatabase(databasetype, databasename);
            }

            //TyphonML
            targetModel = typhonMLInterface.createEntityType(model, firstNewEntity);
            targetModel = typhonMLInterface.createEntityType(targetModel, secondNewEntity);
            relation = new RelationDOImpl("splitRelation", firstNewEntity, secondNewEntity, null, false, CardinalityDO.ONE);
            targetModel = typhonMLInterface.createRelationship(relation, targetModel);
            targetModel = typhonMLInterface.deleteEntityType(sourceEntity.getName(), targetModel);

            //TyphonQL
            typhonQLInterface.createEntityType(firstNewEntity, targetModel);
            typhonQLInterface.createEntityType(secondNewEntity, targetModel);
            typhonQLInterface.createRelationshipType(relation, targetModel);
            //TODO Data Manipulation

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.ENTITY + ", " + ParametersKeyString.FIRSTNEWENTITY + ", " + ParametersKeyString.SECONDNEWENTITY + ", " + ParametersKeyString.DATABASENAME + ", " + ParametersKeyString.DATABASETYPE + "]");
        }
    }


}
