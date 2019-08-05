package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.EntityHandlers.*;
import com.typhon.evolutiontool.handlers.Handler;
import com.typhon.evolutiontool.handlers.RelationHandlers.RelationAddHandler;
import com.typhon.evolutiontool.handlers.RelationHandlers.RelationDisableContainmentHandler;
import com.typhon.evolutiontool.handlers.RelationHandlers.RelationEnableContainmentHandler;
import com.typhon.evolutiontool.handlers.RelationHandlers.RelationRemoveHandler;
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
    private TyphonQLInterface typhonQLInterface;
    @Autowired
    private TyphonMLInterface typhonMLInterface;
    private Model targetModel;

    private Handler entityHandler;
    private Handler relationHandler;

    public EvolutionServiceImpl(){
        // Init chain of responsibility for entity
        Handler entityAdd = new EntityAddHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface);
        Handler entityRemove = new EntityRemoveHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface);
        Handler entityRename = new EntityRenameHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface);
        Handler entityMigrate = new EntityMigrateHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface);
        Handler entitySplitHorizontal = new EntitySplitHorizontalHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface);
        Handler entitySplitVertical = new EntitySplitVerticalHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface);
        Handler entityMerge = new EntityMergeHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface);

        entityHandler = entityAdd;
        entityAdd.setNext(entityRemove);
        entityRemove.setNext(entityRename);
        entityRename.setNext(entityMigrate);
        entityMigrate.setNext(entitySplitHorizontal);
        entitySplitHorizontal.setNext(entitySplitVertical);
        entitySplitVertical.setNext(entityMerge);



        // Init chain of responsibility for Relations
        Handler relationAdd = new RelationAddHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface);
        Handler relationRemove = new RelationRemoveHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface);
        Handler relationEnableContainment = new RelationEnableContainmentHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface);
        Handler relationDisableContainment = new RelationDisableContainmentHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface);

        relationHandler = relationAdd;
        relationAdd.setNext(relationRemove);
        relationRemove.setNext(relationEnableContainment);
        relationEnableContainment.setNext(relationDisableContainment);

    }

    @Override
    public Model evolveEntity(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported{
        return entityHandler.handle(smo, model);
    }

    @Override
    public Model evolveRelation(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported{
        return relationHandler.handle(smo, model);
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
