package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.Relation;
import com.typhon.evolutiontool.entities.TyphonMLSchema;
import com.typhon.evolutiontool.entities.WorkingSet;
import com.typhon.evolutiontool.services.TyphonInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.stream.Collectors;

@Component("typhonql")
public class TyphonInterfaceQLImpl implements TyphonInterface {

    Logger logger = LoggerFactory.getLogger(TyphonInterfaceQLImpl.class);
    private TyphonQLConnection typhonQLConnection;


    public TyphonInterfaceQLImpl() {

    }


    private TyphonQLConnection getTyphonQLConnection(String typhonMLVersion) {
        //TODO Investigate String vs Object typhonML schema.
        return TyphonQLConnection.newEngine(new TyphonMLSchema(typhonMLVersion));
    }

    @Override
    public String createEntityType(Entity newEntity, String typhonMLVersion) {
        //TODO Handling of Identifier, index, etc...
        String tql;
        logger.info("Create entity [{}] via TyphonQL DDL query on TyphonML model [{}] ", newEntity.getName(),typhonMLVersion);
        tql="TQLDDL CREATE ENTITY "+newEntity.getName()+" {"+newEntity.getAttributes().entrySet().stream().map(entry -> entry.getKey()+" "+entry.getValue()).collect(Collectors.joining(","))+"}";
        getTyphonQLConnection(typhonMLVersion).executeTyphonQLDDL(tql);
        return tql;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, String typhonMLVersion) {
        logger.info("Rename Entity [{}] to [{}] via TyphonQL on TyphonML model [{}]", oldEntityName, newEntityName, typhonMLVersion);
        String tql = "TQL DDL RENAME ENTITY "+ oldEntityName +" TO "+ newEntityName;
        getTyphonQLConnection(typhonMLVersion).executeTyphonQLDDL(tql);

    }

    @Override
    public WorkingSet readAllEntityData(Entity entity, String typhonMLVersion) {
        return getTyphonQLConnection(typhonMLVersion).query("from ? e select e", entity.getName());
    }

    /**
     * Retrieve all entity data, all attributes of entity @param entityId using the model provided.
     * @param entityId
     * @param typhonMLVersion
     * @return
     */
    @Override
    public WorkingSet readAllEntityData(String entityId, String typhonMLVersion) {
        return getTyphonQLConnection(typhonMLVersion).query("from ? e select e", entityId);
    }

    @Override
    public WorkingSet readEntityDataEqualAttributeValue(Entity sourceEntity, String attributeName, String attributeValue, String typhonMLVersion) {
        return getTyphonQLConnection(typhonMLVersion).query("from ? e select e where ? = ?", sourceEntity.getName(), attributeName, attributeValue);
    }

    @Override
    public WorkingSet readEntityDataSelectAttributes(String sourceEntityName, List<String> attributes, String typhonMLVersion) {
        return getTyphonQLConnection(typhonMLVersion).query("from ? e select "+attributes.stream().map(a -> "e.".concat(a)).collect(Collectors.joining(",")), sourceEntityName);
    }

    @Override
    public void writeWorkingSetData(WorkingSet workingSetData, String typhonMLVersion) {
        getTyphonQLConnection(typhonMLVersion).insert(workingSetData);
    }

    @Override
    public void addForeignKey(Entity sourceEntity, Entity targetEntity, String targetmodelid, boolean isMandatory, boolean isIdentifier) {

    }

    @Override
    public void createJoinTable(Entity sourceEntity, Entity targetEntity) {

    }

    @Override
    public void deleteForeignKey(Entity sourceEntity, Entity targetEntity) {

    }

    @Override
    public WorkingSet readRelationship(Relation relation, String typhonMLVersion) {
        return getTyphonQLConnection(typhonMLVersion).query("from ? s , ? t select s, t where s.?==? " , relation.getSourceEntity().getName(), relation.getTargetEntity().getName(), relation.getName(),relation.getTargetEntity().getIdentifier());
    }

    @Override
    public WorkingSet deleteRelationship(Relation relation, boolean datadelete, String typhonMLversion) {
        this.deleteForeignKey(relation.getSourceEntity(), relation.getTargetEntity());
        if(datadelete){
            //For nosql document db
            //this.deleteAttributes(relation.getSourceEntity().getName(), Arrays.asList(relation.getName()), typhonMLversion);
            // for relational?
            return getTyphonQLConnection(typhonMLversion).delete(this.readRelationship(relation,typhonMLversion));
        }

        return null;
    }

    /**
     * Deletes all data of given entityid on given TyphonML (by first read them with readAllEntityData function. Uses TyphonQL delete function (D4.2).
     * @param entityid
     * @param typhonMLVersion
     * @return
     */
    @Override
    public WorkingSet deleteAllEntityData(String entityid, String typhonMLVersion) {
        return getTyphonQLConnection(typhonMLVersion).delete(this.readAllEntityData(entityid, typhonMLVersion));
    }


    @Override
    public void deleteEntityStructure(String entityname, String typhonMLVersion) {
        String tql = "TQLDDL DELETE ENTITY " + entityname + " on TyphonML [" + typhonMLVersion + "]";
        logger.info("Delete entity [{}] via TyphonQL DDL on TyphonML model [{}] ", entityname, typhonMLVersion);
        getTyphonQLConnection(typhonMLVersion).executeTyphonQLDDL(tql);
    }

    /**
     * Delete attributes structure and data of given attribute name list in the given entityname .
     * @param entityname
     * @param attributes
     * @param typhonMLVersion
     */
    @Override
    public void deleteAttributes(String entityname, List<String> attributes, String typhonMLVersion) {
        //TODO Separate deletion of data and structure.
        // Delete data
        getTyphonQLConnection(typhonMLVersion).delete(this.readEntityDataSelectAttributes(entityname, attributes, typhonMLVersion));

        //Delete Structure
        String tql = "TQLDDL DELETE ATTRIBUTES " + entityname +", "+ attributes.stream().map(a -> "e.".concat(a)).collect(Collectors.joining(","))+ " on TyphonML [" + typhonMLVersion + "]";
        logger.info("Delete attributes [{}] via TyphonQL DDL on TyphonML model [{}] ", entityname, typhonMLVersion);
        getTyphonQLConnection(typhonMLVersion).executeTyphonQLDDL(tql);
    }

    @Override
    public void deleteWorkingSetData(WorkingSet dataToDelete, String typhonMLVersion) {
        getTyphonQLConnection(typhonMLVersion).delete(dataToDelete);
    }

    /**
     * Depending on the underlying databases. Creates foreign key (for relational) or changes the way the data must be inserted (for NoSQL). See detailed action plan appendix.
     * @param relation
     * @param typhonMLVersion
     */
    @Override
    public void createRelationshipType(Relation relation, String typhonMLVersion) {
        throw new NotImplementedException();
    }


}
