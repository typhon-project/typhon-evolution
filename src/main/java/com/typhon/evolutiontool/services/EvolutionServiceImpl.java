package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import com.typhon.evolutiontool.utils.WorkingSetFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import typhonml.Model;

import java.util.Arrays;
import java.util.List;

/*
    This class implements the operations needed to complete the execution of a Schema Modification Operator (SMO).
    For each operator it will :
     1. Verify that the needed parameters are in the input parameter map of the SMO object.
     2. Execute the structure and data change operations of the SMO operator by calling the TyphonQLInterface object implementation
     3. Modify the TyphonML module that the operator is executed an that the current running TyphonML model can be changed.
 */
@Service
public class EvolutionServiceImpl implements EvolutionService{


    Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);
    @Autowired
    private TyphonDLInterface typhonDLInterface;
    @Autowired
    @Qualifier("typhonql")
    private TyphonQLInterface typhonQLInterface;
    @Autowired
    private TyphonMLInterface typhonMLInterface;
    private Model targetModel;

    @Override
    public Model addEntityType(SMO smo, Model model) throws InputParameterException {
        EntityDO newEntity;
        String databasetype, databasename, logicalname;
        DatabaseType dbtype;
        // Verify ParametersKeyString
        if(containParameters(smo,Arrays.asList(ParametersKeyString.ENTITY,ParametersKeyString.DATABASENAME,ParametersKeyString.DATABASETYPE, ParametersKeyString.TARGETLOGICALNAME))){
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            dbtype = DatabaseType.valueOf(databasetype.toUpperCase());
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();
            logicalname = smo.getInputParameter().get(ParametersKeyString.TARGETLOGICALNAME).toString();
            // Verify that an instance of the underlying database is running in the TyphonDL.
            if (!typhonDLInterface.isDatabaseRunning(databasetype, databasename)) {
                typhonDLInterface.createDatabase(databasetype, databasename);
            }
            //Executing evolution operations
//            newEntity = smo.getPOJOFromInputParameter(ParametersKeyString.ENTITY, EntityDOJsonImpl.class);
            newEntity = smo.getEntityDOFromInputParameter(ParametersKeyString.ENTITY);
            targetModel = typhonMLInterface.createEntityType(model, newEntity);
            targetModel = typhonMLInterface.createDatabase(dbtype, databasename, targetModel);
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(dbtype, databasename, logicalname, newEntity.getName(), targetModel);
            typhonQLInterface.createEntityType(newEntity,targetModel);
            return targetModel;
        }
        else
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.ENTITY+", "+ParametersKeyString.DATABASENAME+", "+ParametersKeyString.DATABASETYPE+", "+ ParametersKeyString.TARGETLOGICALNAME+"]");

    }

    @Override
    public Model removeEntityType(SMO smo, Model model) throws InputParameterException {
        String entityname;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME))) {
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            //If the entity is involved in a relationship. Abort
            if (typhonMLInterface.hasRelationship(entityname,model)) {
                throw new InputParameterException("Cannot delete an entity involved in a relationship. Remove the relationships first.");
            }
            //Delete data
            typhonQLInterface.deleteAllEntityData(entityname,model);
            //Delete structures
            typhonQLInterface.deleteEntityStructure(entityname, model);
            targetModel = typhonMLInterface.deleteEntityType(entityname, model);
            typhonMLInterface.deleteEntityMappings(entityname, model);
            //TODO Delete table mapping in TyphonML.

            return targetModel;
        }else {
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.ENTITYNAME+"]");
        }
    }

    @Override
    public Model renameEntityType(SMO smo, Model model) throws InputParameterException {
        String oldEntityName,newEntityName;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME, ParametersKeyString.NEWENTITYNAME))) {
            oldEntityName = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            newEntityName = smo.getInputParameter().get(ParametersKeyString.NEWENTITYNAME).toString();
            typhonQLInterface.renameEntity(oldEntityName, newEntityName,model);
            targetModel = typhonMLInterface.renameEntity(oldEntityName, newEntityName, model);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.ENTITYNAME+", "+ ParametersKeyString.NEWENTITYNAME +"]");
        }
    }


    /**
     * Migrates the data instances of sourceEntity that has a given attributeValue of their attribute
     * attributeName to a newly created targetEntity with the same structure.
     * This new entity is mapped to a new table/collection/.. in the same database as sourceEntity.
     * @param smo
     * @return
     * @throws InputParameterException
     */
    @Override
    public Model splitHorizontal(SMO smo, Model model) throws InputParameterException {
        String sourceEntityName, targetEntityName, targetLogicalName, attributeName, attributeValue, databasename, databasetype;
        WorkingSet dataSource, dataTarget;
        DatabaseType dbtype;
        dataTarget = WorkingSetFactory.createEmptyWorkingSet();
        if (containParameters(smo, Arrays.asList(ParametersKeyString.SOURCEENTITYNAME, ParametersKeyString.TARGETENTITYNAME, ParametersKeyString.TARGETLOGICALNAME, ParametersKeyString.ATTRIBUTENAME, ParametersKeyString.ATTRIBUTEVALUE, ParametersKeyString.DATABASETYPE, ParametersKeyString.DATABASENAME))) {
            sourceEntityName = smo.getInputParameter().get(ParametersKeyString.SOURCEENTITYNAME).toString();
            targetEntityName = smo.getInputParameter().get(ParametersKeyString.TARGETENTITYNAME).toString();
            targetLogicalName = smo.getInputParameter().get(ParametersKeyString.TARGETLOGICALNAME).toString();
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();
            attributeName = smo.getInputParameter().get(ParametersKeyString.ATTRIBUTENAME).toString();
            attributeValue = smo.getInputParameter().get(ParametersKeyString.ATTRIBUTEVALUE).toString();
            dbtype = DatabaseType.valueOf(databasetype.toUpperCase());
            targetModel = typhonMLInterface.copyEntityType(sourceEntityName, targetEntityName, model);
            targetModel = typhonMLInterface.createDatabase(dbtype, databasename, targetModel);
            // Create a new logical mapping for the created EntityDO type.
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(dbtype,databasename, targetLogicalName, targetEntityName, targetModel);
            dataSource = typhonQLInterface.readEntityDataEqualAttributeValue(sourceEntityName, attributeName, attributeValue, model);
            dataTarget.setEntityRows(targetEntityName,dataSource.getEntityInstanceRows(sourceEntityName));
            typhonQLInterface.writeWorkingSetData(dataTarget, targetModel);
            typhonQLInterface.deleteWorkingSetData(dataSource,model);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.SOURCEENTITYNAME+", "+ParametersKeyString.TARGETENTITYNAME+", "+ ParametersKeyString.TARGETLOGICALNAME+", "+ ParametersKeyString.ATTRIBUTENAME+", "+ ParametersKeyString.ATTRIBUTEVALUE+", "+ ParametersKeyString.DATABASETYPE+", "+ ParametersKeyString.DATABASENAME+"]");
        }
    }

    /**
     * Partially migrates the instances of sourceEntity to a new entity targetEntity. Only the values
     * of attributes [attributesNames] are migrated. The link between the instances of entity1 and entity2 is
     * kept via a new one-to-one relationship relName.
     * @param smo
     * @return
     * @throws InputParameterException
     */
    @Override
    public Model splitVertical(SMO smo, Model model) throws InputParameterException {
        String databasetype, databasename, sourceEntityId;
        RelationDO relation;
        EntityDO sourceEntity, firstNewEntity, secondNewEntity;
        WorkingSet dataSource, dataTarget;
        dataTarget = WorkingSetFactory.createEmptyWorkingSet();
        if (containParameters(smo, Arrays.asList(
                ParametersKeyString.ENTITY,
                ParametersKeyString.FIRSTNEWENTITY,
                ParametersKeyString.SECONDNEWENTITY,
                ParametersKeyString.DATABASENAME,
                ParametersKeyString.DATABASETYPE))) {
            sourceEntity = smo.getEntityDOFromInputParameter(ParametersKeyString.ENTITY);
            firstNewEntity = smo.getEntityDOFromInputParameter(ParametersKeyString.FIRSTNEWENTITY);
            secondNewEntity = smo.getEntityDOFromInputParameter(ParametersKeyString.SECONDNEWENTITY);
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();

            //TyphonDL
            if (!typhonDLInterface.isDatabaseRunning(databasetype, databasename)) {
                typhonDLInterface.createDatabase(databasetype, databasename);
            }

            //TyphonML
            targetModel = typhonMLInterface.createEntityType(model, firstNewEntity);
            targetModel = typhonMLInterface.createEntityType(targetModel, secondNewEntity);
            relation = RelationDOFactory.createRelationDO("splitRelation", firstNewEntity, secondNewEntity, null, false, CardinalityDO.ONE);
            targetModel = typhonMLInterface.createRelationship(relation,targetModel);
            targetModel = typhonMLInterface.deleteEntityType(sourceEntity.getName(), targetModel);

            //TyphonQL
            typhonQLInterface.createEntityType(firstNewEntity, targetModel);
            typhonQLInterface.createEntityType(secondNewEntity, targetModel);
            typhonQLInterface.createRelationshipType(relation, targetModel);
            //TODO Data Manipulation

            return targetModel;
        }else{
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.ENTITY+", "+ParametersKeyString.FIRSTNEWENTITY+", "+ParametersKeyString.SECONDNEWENTITY+", "+ParametersKeyString.DATABASENAME+", "+ParametersKeyString.DATABASETYPE+"]");
        }
    }


    /**
     * Migrates data of entity in sourceModel (read) to entity in targetModel (write).
     * Data is then deleted from sourceModel.
     * @param smo
     * @return
     * @throws InputParameterException
     */
    @Override
    public Model migrateEntity(SMO smo, Model model) throws InputParameterException {
        typhonml.Entity entity;
        String entityname, databasetype, databasename, targetLogicalName;
        DatabaseType dbtype;
        WorkingSet data;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME, ParametersKeyString.DATABASENAME, ParametersKeyString.DATABASETYPE, ParametersKeyString.TARGETLOGICALNAME))) {
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            dbtype = DatabaseType.valueOf(databasetype.toUpperCase());
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();
            targetLogicalName = smo.getInputParameter().get(ParametersKeyString.TARGETLOGICALNAME).toString();
            entity = typhonMLInterface.getEntityTypeFromName(entityname, model);
            // Verify that an instance of the underlying database is running in the TyphonDL.
            if (!typhonDLInterface.isDatabaseRunning(databasetype, databasename)) {
                typhonDLInterface.createDatabase(databasetype, databasename);
            }
            targetModel = typhonMLInterface.deleteEntityMappings(entityname, model);
            targetModel = typhonMLInterface.createDatabase(dbtype, databasename, targetModel);
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(dbtype,databasename, targetLogicalName, entityname, targetModel);
            typhonQLInterface.createEntityType(entity, targetModel);
            data = typhonQLInterface.readAllEntityData(entityname,model);
            typhonQLInterface.writeWorkingSetData(data,targetModel);
            typhonQLInterface.deleteWorkingSetData(data, model);
            typhonQLInterface.deleteEntityStructure(entityname, model);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.ENTITYNAME+", "+ ParametersKeyString.DATABASENAME+", "+  ParametersKeyString.DATABASETYPE+", "+  ParametersKeyString.TARGETLOGICALNAME+"]");
        }
    }

    @Override
    public String mergeEntities(SMO smo, Model model) throws InputParameterException {
        //TODO
        /*
        TyphonML :
        - Check cardinality between the two entities (one_to_many only)
        - Delete relation between the two
        - Check that second entity is not any relationship. If yes, cancel.
        - Copy attribute of second entity
        - Rename entity.

        - TyphonQL :
        -
         */
        return null;
    }

    @Override
    public Model addRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation;
        String targetmodelid;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION))) {
            relation = smo.getRelationDOFromInputParameter(ParametersKeyString.RELATION);
            targetModel = typhonMLInterface.createRelationship(relation, model);
            typhonQLInterface.createRelationshipType(relation,targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter. Needed [" + ParametersKeyString.RELATION+"]");
        }
    }

    @Override
    public Model removeRelationship(SMO smo, Model model) throws InputParameterException {
        String relationname;
        String entityname;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATIONNAME, ParametersKeyString.ENTITYNAME))) {
            relationname = smo.getInputParameter().get(ParametersKeyString.RELATIONNAME).toString();
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            targetModel = typhonMLInterface.deleteRelationshipInEntity(relationname, entityname, model);
            typhonQLInterface.deleteRelationshipInEntity(relationname, entityname, targetModel);
            return targetModel;
        }else {
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.RELATIONNAME+", "+ParametersKeyString.ENTITYNAME+"]");
        }
    }

    @Override
    public Model enableContainmentInRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION))) {
            relation = smo.getRelationDOFromInputParameter(ParametersKeyString.RELATION);
            if (typhonMLInterface.getDatabaseType(relation.getSourceEntity().getName(),model) == DatabaseType.RELATIONALDB) {
                throw new InputParameterException("Cannot produce a containment relationship in relational database source entity");
            }
            targetModel = typhonMLInterface.enableContainment(relation, model);
            typhonQLInterface.enableContainment(relation.getName(),relation.getSourceEntity().getName(), targetModel);
            return targetModel;
        }
        else{
            throw new InputParameterException("Missing parameter. Needed [" + ParametersKeyString.RELATION+"]");
        }
    }

    @Override
    public Model disableContainmentInRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION))) {
            relation = smo.getRelationDOFromInputParameter(ParametersKeyString.RELATION);
            targetModel = typhonMLInterface.disableContainment(relation, model);
            typhonQLInterface.disableContainment(relation.getName(),relation.getSourceEntity().getName(), targetModel);
            return targetModel;
        }
        else{
            throw new InputParameterException("Missing parameter. Needed [" + ParametersKeyString.RELATION+"]");
        }
    }

    @Override
    public Model enableOppositeRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation,oppositeRel;
        String entityname, relationname;
        //TODO Add the new Relation name. Change arg to Relation instead of relationname and entityname.
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME,ParametersKeyString.RELATIONNAME))) {
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            relationname = smo.getInputParameter().get(ParametersKeyString.RELATIONNAME).toString();
            relation = RelationDOFactory.createRelationDOFromRelationML(typhonMLInterface.getRelationFromNameInEntity(relationname, entityname, model));
            if (relation == null) {
                throw new InputParameterException("No existing relationship with name ["+ relationname+"] in entity ["+entityname+"]");
            }
//            Quid opposite cardinality? TODO
            oppositeRel = new RelationDOJsonImpl("opposite - " + relation.getName(), relation.getTargetEntity(), relation.getSourceEntity(), relation, false, relation.getCardinality());
            targetModel = typhonMLInterface.createRelationship(oppositeRel, model);
            typhonQLInterface.createRelationshipType(oppositeRel, targetModel);

            return targetModel;
        }else{
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.RELATIONNAME+", "+ParametersKeyString.ENTITYNAME+"]");
        }
    }

    @Override
    public Model disableOppositeRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation, oppositeRel;
        String relationname, sourcemodelid, entityname;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME,ParametersKeyString.RELATIONNAME))) {
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            relationname = smo.getInputParameter().get(ParametersKeyString.RELATIONNAME).toString();
            relation = RelationDOFactory.createRelationDOFromRelationML(typhonMLInterface.getRelationFromNameInEntity(relationname, entityname, model));
            if (relation == null) {
                throw new InputParameterException("No existing relationship with name ["+ relationname+"] in entity ["+entityname+"]");
            }
            oppositeRel = relation.getOpposite();
            targetModel = typhonMLInterface.deleteRelationshipInEntity(oppositeRel.getName(), relation.getTargetEntity().getName(), model);
            typhonQLInterface.deleteRelationshipInEntity(oppositeRel.getName(),relation.getTargetEntity().getName(), targetModel);
            return targetModel;
        }
        else{
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.RELATIONNAME+", "+ParametersKeyString.ENTITYNAME+"]");
        }
    }

    @Override
    public Model changeCardinality(SMO smo, Model model) throws InputParameterException {
        RelationDO relation;
        CardinalityDO cardinality;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION, ParametersKeyString.CARDINALITY))) {
            relation = smo.getRelationDOFromInputParameter(ParametersKeyString.RELATION);
            cardinality = CardinalityDO.getByName(smo.getInputParameter().get(ParametersKeyString.CARDINALITY).toString());
            targetModel = typhonMLInterface.changeCardinalityInRelation(relation, cardinality, model);
            typhonQLInterface.changeCardinalityInRelation(relation.getName(), relation.getSourceEntity().getName(), cardinality, targetModel);
            return targetModel;
        }else{
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.RELATION+", "+ParametersKeyString.CARDINALITY+"]");
        }
    }

    @Override
    public Model addAttribute(SMO smo, Model model) throws InputParameterException {
        AttributeDO attributeDO;
        String entityname;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME, ParametersKeyString.ATTRIBUTE))) {
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            attributeDO = smo.getAttributeDOFromInputParameter(ParametersKeyString.ATTRIBUTE);
            targetModel = typhonMLInterface.addAttribute(attributeDO, entityname);
            typhonQLInterface.addAttribute(attributeDO, entityname, targetModel);
            return targetModel;
        }else {
            throw new InputParameterException("Missing parameter. ");
        }
    }

    @Override
    public String removeAttribute(SMO smo, Model model) {
        return null;
    }

    @Override
    public String renameAttribute(SMO smo, Model model) {
        return null;
    }

    @Override
    public String changeTypeAttribute(SMO smo, Model model) {
        return null;
    }

    @Override
    public String addIdentifier(SMO smo, Model model) {
        return null;
    }

    @Override
    public String addComponentToIdentifier(SMO smo, Model model) {
        return null;
    }

    @Override
    public String removeIdentifier(SMO smo, Model model) {
        return null;
    }

    @Override
    public String removeComponentToIdentifier(SMO smo, Model model) {
        return null;
    }

    @Override
    public String addIndex(SMO smo, Model model) {
        return null;
    }

    @Override
    public String removeIndex(SMO smo, Model model) {
        return null;
    }

    @Override
    public String addComponentToIndex(SMO smo, Model model) {
        return null;
    }

    @Override
    public String removeComponentToIndex(SMO smo, Model model) {
        return null;
    }

    @Override
    public String renameRelationalTable(SMO smo, Model model) {
        return null;
    }

    @Override
    public String renameDocumentCollection(SMO smo, Model model) {
        return null;
    }

    @Override
    public String renameColumnFamilyName(SMO smo, Model model) {
        return null;
    }

    public boolean containParameters(SMO smo, List<String> parameters) {
        logger.info("Verifying input parameter for [{}] - [{}] operator",smo.getTyphonObject(), smo.getEvolutionOperator());
        return smo.inputParametersContainsExpected(parameters);
    }

    public void setTyphonDLInterface(TyphonDLInterface typhonDLInterface) {
        this.typhonDLInterface = typhonDLInterface;
    }

    public void setTyphonQLInterface(TyphonQLInterface typhonQLInterface) {
        this.typhonQLInterface = typhonQLInterface;
    }

    public void setTyphonMLInterface(TyphonMLInterface typhonMLInterface) {
        this.typhonMLInterface = typhonMLInterface;
    }
}
