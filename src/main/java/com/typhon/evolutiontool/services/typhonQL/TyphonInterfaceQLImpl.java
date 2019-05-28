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
import typhonml.Model;

import java.util.List;
import java.util.stream.Collectors;

@Component("typhonql")
public class TyphonInterfaceQLImpl implements TyphonInterface {

    Logger logger = LoggerFactory.getLogger(TyphonInterfaceQLImpl.class);
    private TyphonQLConnection typhonQLConnection;


    public TyphonInterfaceQLImpl() {

    }


    private TyphonQLConnection getTyphonQLConnection(Model model) {
        //TODO Model vs TyphonMLSchema specif?
        return TyphonQLConnection.newEngine(new TyphonMLSchema(model.toString()));
    }

    @Override
    public String createEntityType(Entity newEntity, Model model) {
        //TODO Handling of Identifier, index, etc...
        String tql;
        logger.info("Create entity [{}] via TyphonQL DDL query on TyphonML model [{}] ", newEntity.getName(),model);
        tql="TQLDDL CREATE ENTITY "+newEntity.getName()+" {"+newEntity.getAttributes().entrySet().stream().map(entry -> entry.getKey()+" "+entry.getValue()).collect(Collectors.joining(","))+"}";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
        return tql;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, Model model) {
        logger.info("Rename Entity [{}] to [{}] via TyphonQL on TyphonML model [{}]", oldEntityName, newEntityName, model);
        String tql = "TQL DDL RENAME ENTITY "+ oldEntityName +" TO "+ newEntityName;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);

    }

    @Override
    public WorkingSet readAllEntityData(Entity entity, Model model) {
        return getTyphonQLConnection(model).query("from ? e select e", entity.getName());
    }

    /**
     * Retrieve all entity data, all attributes of entity @param entityId using the model provided.
     * @param entityId
     * @param model
     * @return
     */
    @Override
    public WorkingSet readAllEntityData(String entityId, Model model) {
        return getTyphonQLConnection(model).query("from ? e select e", entityId);
    }

    @Override
    public WorkingSet readEntityDataEqualAttributeValue(Entity sourceEntity, String attributeName, String attributeValue, Model model) {
        return getTyphonQLConnection(model).query("from ? e select e where ? = ?", sourceEntity.getName(), attributeName, attributeValue);
    }

    @Override
    public WorkingSet readEntityDataSelectAttributes(String sourceEntityName, List<String> attributes, Model model) {
        return getTyphonQLConnection(model).query("from ? e select "+attributes.stream().map(a -> "e.".concat(a)).collect(Collectors.joining(",")), sourceEntityName);
    }

    @Override
    public WorkingSet deleteAllEntityData(String entityid, Model model) {
        //TODO Implement
        return null;
    }

    @Override
    public void writeWorkingSetData(WorkingSet workingSetData, Model model) {
        getTyphonQLConnection(model).insert(workingSetData);
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
    public WorkingSet readRelationship(Relation relation, Model model) {
        return getTyphonQLConnection(model).query("from ? s , ? t select s, t where s.?==? " , relation.getSourceEntity().getName(), relation.getTargetEntity().getName(), relation.getName(),relation.getTargetEntity().getIdentifier());
    }

    @Override
    public WorkingSet deleteRelationship(Relation relation, boolean datadelete, Model model) {
        this.deleteForeignKey(relation.getSourceEntity(), relation.getTargetEntity());
        if(datadelete){
            //For nosql document db
            //this.deleteAttributes(relation.getSourceEntity().getName(), Arrays.asList(relation.getName()), model);
            // for relational?
            return getTyphonQLConnection(model).delete(this.readRelationship(relation,model));
        }

        return null;
    }

    @Override
    public void deleteEntityStructure(String entityname, Model model) {
        String tql = "TQLDDL DELETE ENTITY " + entityname + " on TyphonML [" + model + "]";
        logger.info("Delete entity [{}] via TyphonQL DDL on TyphonML model [{}] ", entityname, model);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    /**
     * Delete attributes structure and data of given attribute name list in the given entityname .
     * @param entityname
     * @param attributes
     * @param model
     */
    @Override
    public void deleteAttributes(String entityname, List<String> attributes, Model model) {
        //TODO Separate deletion of data and structure.
        // Delete data
        getTyphonQLConnection(model).delete(this.readEntityDataSelectAttributes(entityname, attributes, model));

        //Delete Structure
        String tql = "TQLDDL DELETE ATTRIBUTES " + entityname +", "+ attributes.stream().map(a -> "e.".concat(a)).collect(Collectors.joining(","))+ " on TyphonML [" + model + "]";
        logger.info("Delete attributes [{}] via TyphonQL DDL on TyphonML model [{}] ", entityname, model);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void deleteWorkingSetData(WorkingSet dataToDelete, Model model) {
        getTyphonQLConnection(model).delete(dataToDelete);
    }

    /**
     * Depending on the underlying databases. Creates foreign key (for relational) or changes the way the data must be inserted (for NoSQL). See detailed action plan appendix.
     * @param relation
     * @param model
     */
    @Override
    public void createRelationshipType(Relation relation, Model model) {
        throw new NotImplementedException();
    }


}
