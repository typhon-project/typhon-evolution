package com.typhon.evolutiontool.services.typhonQL;


import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typhon.evolutiontool.entities.AttributeDO;
import com.typhon.evolutiontool.entities.CardinalityDO;
import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.RelationDO;
import com.typhon.evolutiontool.entities.WorkingSet;
import typhonml.Model;


public class TyphonInterfaceQLImpl implements TyphonQLInterface {

    Logger logger = LoggerFactory.getLogger(TyphonInterfaceQLImpl.class);

    public TyphonInterfaceQLImpl() {

    }


    private TyphonQLConnection getTyphonQLConnection(Model model) {
        //TODO Model vs TyphonMLSchema specif?
        return TyphonQLConnection.newEngine(model);
    }

    @Override
    public String createEntityType(EntityDO newEntity, Model model) {
        //TODO Handling of Identifier, index, etc...
        String tql;
        logger.info("Create entity [{}] via TyphonQL DDL query on TyphonML model [{}] ", newEntity.getName(), model);
        tql = "TQLDDL CREATE ENTITY " + newEntity.getName() + " {" + newEntity.getAttributes().entrySet().stream().map(entry -> entry.getKey() + " " + entry.getValue()).collect(Collectors.joining(",")) + "}";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
        return tql;
    }

    @Override
    public String createEntityType(typhonml.Entity newEntity, Model model) {
        //TODO Handling of Identifier, index, etc...
        String tql;
        logger.info("Create entity [{}] via TyphonQL DDL query on TyphonML model [{}] ", newEntity.getName(), model);
        tql = "TQLDDL CREATE ENTITY " + newEntity.getName() + " {" + newEntity.getAttributes().stream().map(attribute -> attribute.getName() + " " + attribute.getType()).collect(Collectors.joining(",")) + "}";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
        return tql;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, Model model) {
        logger.info("Rename EntityDO [{}] to [{}] via TyphonQL on TyphonML model [{}]", oldEntityName, newEntityName, model);
        String tql = "TQL DDL RENAME ENTITY " + oldEntityName + " TO " + newEntityName;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);

    }

    @Override
    public WorkingSet readAllEntityData(EntityDO entity, Model model) {
        return getTyphonQLConnection(model).query("from %s e select e", entity.getName());
    }

    /**
     * Retrieve all entity data, all attributes of entity @param entityId using the model provided.
     *
     * @param entityId
     * @param model
     * @return
     */
    @Override
    public WorkingSet readAllEntityData(String entityId, Model model) {
        return getTyphonQLConnection(model).query("from %s e select e", entityId);
    }

    @Override
    public WorkingSet readEntityDataEqualAttributeValue(String sourceEntityName, String attributeName, String attributeValue, Model model) {
        return getTyphonQLConnection(model).query("from %s e select e where %s = %s", sourceEntityName, attributeName, attributeValue);
    }

    @Override
    public WorkingSet readEntityDataSelectAttributes(String sourceEntityName, List<String> attributes, Model model) {
        return getTyphonQLConnection(model).query("from %s e select " + attributes.stream().map(a -> "e.".concat(a)).collect(Collectors.joining(",")), sourceEntityName);
    }

    @Override
    public WorkingSet deleteAllEntityData(String entityid, Model model) {
        return getTyphonQLConnection(model).delete(this.readAllEntityData(entityid, model));
    }

    @Override
    public void writeWorkingSetData(WorkingSet workingSetData, Model model) {
        getTyphonQLConnection(model).insert(workingSetData);
    }

    @Override
    public void deleteForeignKey(EntityDO sourceEntity, EntityDO targetEntity) {

    }

    @Override
    public WorkingSet readRelationship(RelationDO relation, Model model) {
        return getTyphonQLConnection(model).query("from %s s , %s t select s, t where s.%s==%s ", relation.getSourceEntity().getName(), relation.getTargetEntity().getName(), relation.getName(), relation.getTargetEntity().getIdentifier());
    }

    @Override
    public void deleteRelationship(RelationDO relation, boolean datadelete, Model model) {
        this.deleteForeignKey(relation.getSourceEntity(), relation.getTargetEntity());
        if (datadelete) {
            //For nosql document db
            //this.deleteAttributes(relation.getSourceEntity().getName(), Arrays.asList(relation.getName()), model);
            // for relational?
            getTyphonQLConnection(model).delete(this.readRelationship(relation, model));
        }
    }

    @Override
    public void deleteRelationshipInEntity(String relationname, String entityname, Model model) {
        logger.info("Delete Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationname, entityname, model);
        String tql = "TQL DDL DELETE RELATION " + relationname + " IN " + entityname;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void enableContainment(String relationName, String entityname, Model model) {
        logger.info("Enabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationName, entityname, model);
        String tql = "dummy enable containment TQL DDL";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void disableContainment(String relationName, String entityname, Model model) {
        logger.info("Disabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationName, entityname, model);
        String tql = "dummy disable containment TQL DDL";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void changeCardinalityInRelation(String relationname, String entityname, CardinalityDO cardinality, Model model) {
        logger.info("Change cardinality of Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationname, entityname, model);
        String tql = "TQL DDL dummy change relation cardinality";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void addAttribute(AttributeDO attributeDO, String entityname, Model model) {
        logger.info("Add attribute [{}] to entity [{}]  via TyphonQL on TyphonML model [{}]", attributeDO.getName(), entityname, model);
        String tql = "TQL DDL ADD ATTRIBUTE " + attributeDO.getName() + " IN " + entityname;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void renameRelation(String relationName, String newRelationName, Model model) {
        logger.info("Rename Relation [{}] to [{}] via TyphonQL on TyphonML model [{}]", relationName, newRelationName, model);
        String tql = "TQL DDL RENAME RELATION " + relationName + " TO " + newRelationName;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void renameAttribute(String oldAttributeName, String newAttributeName, String entityName, Model model) {
        logger.info("Rename attribute [from '{}' to '{}'] in entity [{}]  via TyphonQL on TyphonML model [{}]", oldAttributeName, newAttributeName, entityName, model);
        String tql = "TQL DDL RENAME ATTRIBUTE " + oldAttributeName + " TO " + newAttributeName + " IN " + entityName;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void changeTypeAttribute(AttributeDO attribute, String entityName, Model model) {
        logger.info("Change type attribute ['{}' to '{}' type] in entity [{}]  via TyphonQL on TyphonML model [{}]", attribute.getName(), attribute.getDataTypeDO().getName(), entityName, model);
        String tql = "TQL DDL UPDATE ATTRIBUTE " + attribute.getName() + " SET TYPE TO " + attribute.getDataTypeDO().getName() + " IN " + entityName;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void deleteEntityStructure(String entityname, Model model) {
        String tql = "TQLDDL DELETE ENTITY " + entityname + " on TyphonML [" + model + "]";
        logger.info("Delete entity [{}] via TyphonQL DDL on TyphonML model [{}] ", entityname, model);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    /**
     * Delete attributes structure and data of given attribute name list in the given entityname .
     *
     * @param entityname
     * @param attributes
     * @param model
     */
    @Override
    public void removeAttributes(String entityname, List<String> attributes, Model model) {
        //TODO Separate deletion of data and structure.
        // Delete data
        getTyphonQLConnection(model).delete(this.readEntityDataSelectAttributes(entityname, attributes, model));

        //Delete Structure
        String tql = "TQLDDL DELETE ATTRIBUTES " + entityname + ", " + attributes.stream().map(a -> "e.".concat(a)).collect(Collectors.joining(",")) + " on TyphonML [" + model + "]";
        logger.info("Delete attributes [{}] via TyphonQL DDL on TyphonML model [{}] ", entityname, model);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void deleteWorkingSetData(WorkingSet dataToDelete, Model model) {
        getTyphonQLConnection(model).delete(dataToDelete);
    }

    /**
     * Depending on the underlying databases. Creates foreign key (for relational) or changes the way the data must be inserted (for NoSQL). See detailed action plan appendix.
     *
     * @param relation
     * @param model
     */
    @Override
    public void createRelationshipType(RelationDO relation, Model model) {
        String tql;
        logger.info("Create relationship [{}] via TyphonQL DDL query on TyphonML model [{}] ", relation.getName(), model);
        tql = "TQLDDL CREATE RELATIONSHIP " + relation;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }


}
