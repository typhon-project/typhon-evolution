package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.utils.WorkingSetFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import typhonml.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    This class implements the operations needed to complete the execution of a Schema Modification Operator (SMO).
    For each operator it will :
     1. Verify that the needed parameters are in the input parameter map of the SMO object.
     2. Execute the structure and data change operations of the SMO operator by calling the TyphonInterface object implementation
     3. Modify the TyphonML module that the operator is executed an that the current running TyphonML model can be changed.
 */
@Service
public class EvolutionServiceImpl implements EvolutionService{


    Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);
    @Autowired
    private TyphonDLInterface typhonDLInterface;
    @Autowired
    @Qualifier("typhonql")
    private TyphonInterface typhonInterface;
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
            typhonInterface.createEntityType(newEntity,targetModel);
            return targetModel;
        }
        else
            throw new InputParameterException("Missing parameter");

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
            typhonInterface.deleteAllEntityData(entityname,model);
            //Delete structures
            typhonInterface.deleteEntityStructure(entityname, model);
            targetModel = typhonMLInterface.deleteEntityType(entityname, model);

            return targetModel;
        }else {
            throw new InputParameterException("Missing parameter");
        }
    }

    @Override
    public Model renameEntityType(SMO smo, Model model) throws InputParameterException {
        String oldEntityName,newEntityName;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.OLDENTITYNAME, ParametersKeyString.NEWENTITYNAME))) {
            oldEntityName = smo.getInputParameter().get(ParametersKeyString.OLDENTITYNAME).toString();
            newEntityName = smo.getInputParameter().get(ParametersKeyString.NEWENTITYNAME).toString();
            typhonInterface.renameEntity(oldEntityName, newEntityName,model);
            targetModel = typhonMLInterface.renameEntity(oldEntityName, newEntityName, model);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter");
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
            dataSource = typhonInterface.readEntityDataEqualAttributeValue(sourceEntityName, attributeName, attributeValue, model);
            dataTarget.setEntityRows(targetEntityName,dataSource.getEntityInstanceRows(sourceEntityName));
            typhonInterface.writeWorkingSetData(dataTarget, targetModel);
            typhonInterface.deleteWorkingSetData(dataSource,model);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter");
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
    public String splitVertical(SMO smo, Model model) throws InputParameterException {
        //TODO
        String sourceEntityName, sourcemodelid, targetmodelid, databasetype, databasename, sourceEntityId;
        RelationDO relation;
        EntityDO targetEntity, sourceEntity;
        List<String> attributes;
        WorkingSet dataSource, dataTarget;
        dataTarget = WorkingSetFactory.createEmptyWorkingSet();
        if (containParameters(smo, Arrays.asList(
                ParametersKeyString.ENTITY,
                ParametersKeyString.TARGETMODEL,
                ParametersKeyString.SOURCEMODEL,
                ParametersKeyString.DATABASENAME,
                ParametersKeyString.DATABASETYPE,
                ParametersKeyString.ENTITYNAME,
                ParametersKeyString.ATTRIBUTES))) {
            sourceEntityName = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            targetEntity = smo.getPOJOFromInputParameter(ParametersKeyString.ENTITY, EntityDOJsonImpl.class);
            attributes = smo.getPOJOFromInputParameter(ParametersKeyString.ATTRIBUTES, ArrayList.class);
            targetmodelid = smo.getInputParameter().get(ParametersKeyString.TARGETMODEL).toString();
            sourcemodelid = smo.getInputParameter().get(ParametersKeyString.SOURCEMODEL).toString();
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();
//            sourceEntity = typhonMLInterface.getEntityTypeFromName(sourceEntityName,sourcemodelid);
            if (!typhonDLInterface.isDatabaseRunning(databasetype, databasename)) {
                typhonDLInterface.createDatabase(databasetype, databasename);
            }

//            relation = new RelationDO("splitVerticalResult", sourceEntity, targetEntity, null, false, CardinalityDO.ONE_ONE);
//            typhonInterface.createEntityType(targetEntity, targetmodelid);
//            this.createRelationshipType(relation, targetmodelid);
            sourceEntityId = typhonMLInterface.getAttributeIdOfEntityType(sourceEntityName);
            attributes.add(sourceEntityId);
//            dataSource = typhonInterface.readEntityDataSelectAttributes(sourceEntityName, attributes, sourcemodelid);
//            dataTarget.setEntityRows(targetEntity.getName(), dataSource.getEntityInstanceRows(sourceEntityName));
//            typhonInterface.writeWorkingSetData(dataTarget,targetmodelid);
            attributes.remove(sourceEntityId);
//            typhonInterface.deleteAttributes(sourceEntityName, attributes, sourcemodelid);

            typhonMLInterface.setNewTyphonMLModel(targetmodelid);

            return "entity splitted vertically";
        }

        return null;
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
            typhonInterface.createEntityType(entity, targetModel);
            data = typhonInterface.readAllEntityData(entityname,model);
            typhonInterface.writeWorkingSetData(data,targetModel);
            typhonInterface.deleteWorkingSetData(data, model);
            typhonInterface.deleteEntityStructure(entityname, model);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter");
        }
    }

    @Override
    public String mergeEntities(SMO smo, Model model) throws InputParameterException {
        return null;
    }

    @Override
    public Model addRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation;
        String targetmodelid;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION))) {
            relation = smo.getRelationDOFromInputParameter(ParametersKeyString.RELATION);
            targetModel = typhonMLInterface.createRelationship(relation, model);
            typhonInterface.createRelationshipType(relation,targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter");
        }
    }

    @Override
    public Model removeRelationship(SMO smo, Model model) {
        String relationname;
        String entityname;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATIONNAME, ParametersKeyString.ENTITYNAME))) {
            relationname = smo.getInputParameter().get(ParametersKeyString.RELATIONNAME).toString();
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            targetModel = typhonMLInterface.deleteRelationshipInEntity(relationname, entityname, model);
            typhonInterface.deleteRelationshipInEntity(relationname, entityname, targetModel);
            return targetModel;
        }
        return null;
    }

    @Override
    public String enableContainmentInRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation;
        String sourcemodelid, targetmodelid;
        WorkingSet ws;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.TARGETMODEL,ParametersKeyString.RELATION, ParametersKeyString.DATADELETE))) {
            relation = smo.getPOJOFromInputParameter(ParametersKeyString.RELATION, RelationDOJsonImpl.class);
            sourcemodelid = smo.getInputParameter().get(ParametersKeyString.SOURCEMODEL).toString();
            targetmodelid = smo.getInputParameter().get(ParametersKeyString.TARGETMODEL).toString();
//            if (typhonMLInterface.getDatabaseType(relation.getSourceEntity().getName()) instanceof RelationalDB) {
//                throw new InputParameterException("Cannot produce a containment relationship in relational database source entity");
//            }
//            ws = typhonInterface.readRelationship(relation,sourcemodelid);
//            typhonInterface.writeWorkingSetData(ws, targetmodelid);
//            typhonInterface.deleteRelationshipInEntity(relation, true, sourcemodelid);
            // = delete EntityDO if relational. TODO
//            typhonMLInterface.setNewTyphonMLModel(targetmodelid);
            return "Relationship containement enabled";
        }
        return null;
    }

    @Override
    public String disableContainmentInRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation;
        String sourcemodelid, targetmodelid;
        WorkingSet ws;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.TARGETMODEL,ParametersKeyString.RELATION))) {
            relation = smo.getPOJOFromInputParameter(ParametersKeyString.RELATION, RelationDOJsonImpl.class);
            sourcemodelid = smo.getInputParameter().get(ParametersKeyString.SOURCEMODEL).toString();
            targetmodelid = smo.getInputParameter().get(ParametersKeyString.TARGETMODEL).toString();
//            if (typhonMLInterface.getDatabaseType(relation.getSourceEntity().getName()) instanceof RelationalDB) {
//                throw new InputParameterException("Please use splitHorizontal operation in case of relational model");
//            }
//            ws = typhonInterface.readRelationship(relation,sourcemodelid);
//            typhonInterface.createEntityType(relation.getTargetEntity(),targetmodelid);
//            typhonInterface.writeWorkingSetData(ws, targetmodelid);
//            typhonInterface.deleteRelationshipInEntity(relation, true, sourcemodelid);
            typhonMLInterface.setNewTyphonMLModel(targetmodelid);
            return "Relationship containement disabled";
        }
        else{
            throw new InputParameterException("Missing parameter");
        }
    }

    @Override
    public String enableOppositeRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation, oppositeRel;
        String entityname, relationname;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME,ParametersKeyString.RELATIONNAME))) {
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            relationname = smo.getInputParameter().get(ParametersKeyString.RELATIONNAME).toString();
            relation = typhonMLInterface.getRelationFromNameInEntity(relationname, entityname, model);
            if (relation == null) {
                throw new InputParameterException("No existing relationship with name provided relationship name");
            }
            //Quid opposite cardinality? TODO
            oppositeRel = new RelationDOJsonImpl("opposite - " + relation.getName(), relation.getTargetEntity(), relation.getSourceEntity(), relation, false, relation.getCardinality());
//            this.createRelationshipType(oppositeRel, targetmodelid);

        }else{
            throw new InputParameterException("Missing parameters");
        }
        return null;
    }

    @Override
    public String disableOppositeRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation, oppositeRel;
        String relationname, sourcemodelid, entityname;
        boolean datadelete;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME,ParametersKeyString.RELATIONNAME, ParametersKeyString.DATADELETE))) {
            sourcemodelid = smo.getInputParameter().get(ParametersKeyString.SOURCEMODEL).toString();
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            relationname = smo.getInputParameter().get(ParametersKeyString.RELATIONNAME).toString();
            datadelete = Boolean.parseBoolean(smo.getInputParameter().get(ParametersKeyString.DATADELETE).toString());
            relation = typhonMLInterface.getRelationFromNameInEntity(relationname,entityname,model);
            if (relation == null) {
                throw new InputParameterException("No existing relationship with name provided relationship name");
            }
            oppositeRel = relation.getOpposite();
//            typhonInterface.deleteRelationshipInEntity(oppositeRel, datadelete, sourcemodelid);
            return "opposite relationship deleted";
        }
        else{
            throw new InputParameterException("Missing parameter");
        }
    }

    @Override
    public String changeCardinality(SMO smo, Model model) {
        return null;
    }

    @Override
    public String addAttribute(SMO smo, Model model) {
        return null;
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

    private void createRelationshipType(RelationDO relation, String targetmodelid) {
        // Implement here rules detailed in appendix file about actions on specific datamodels.

        //If source & target are on relational
//        if(typhonMLInterface.getDatabaseType(relation.getSourceEntity().getName()) instanceof RelationalDB &&
//                typhonMLInterface.getDatabaseType(relation.getTargetEntity().getName()) instanceof RelationalDB)
//            switch (relation.getCardinality()) {
//                case N_N:
//                    typhonInterface.createJoinTable(relation.getSourceEntity(), relation.getTargetEntity());
//                    break;
//                case ONE_N:
//                    typhonInterface.addForeignKey(relation.getTargetEntity(), relation.getSourceEntity(),targetmodelid, true, false);
//                    break;
//                case ZERO_ONE:
//                    typhonInterface.addForeignKey(relation.getSourceEntity(), relation.getTargetEntity(),targetmodelid, false, true);
//                    break;
//                case ONE_ONE:
//                    typhonInterface.addForeignKey(relation.getSourceEntity(), relation.getTargetEntity(),targetmodelid, true, true);
//                    //+ data verification rule (all ids must referenced as fk).
//                    break;
//                case ZERO_N:
//                    typhonInterface.addForeignKey(relation.getTargetEntity(), relation.getSourceEntity(),targetmodelid, false, false);
//            } else{
//            // No specific action, but changes the way data is inserted. (Construction of key value pairs, or adding of reference attribute data.
//        }
    }

    public void setTyphonDLInterface(TyphonDLInterface typhonDLInterface) {
        this.typhonDLInterface = typhonDLInterface;
    }

    public void setTyphonInterface(TyphonInterface typhonInterface) {
        this.typhonInterface = typhonInterface;
    }

    public void setTyphonMLInterface(TyphonMLInterface typhonMLInterface) {
        this.typhonMLInterface = typhonMLInterface;
    }
}
