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

import java.util.*;

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
    private TyphonDLInterface typhonDLInterface;
    private TyphonQLInterface typhonQLInterface;
    private TyphonMLInterface typhonMLInterface;

    Handler entityRename;
    private Model targetModel;


    private Map<EvolutionOperator, Handler> entityHandlers;
    private Map<EvolutionOperator, Handler> relationHandlers;



    @Autowired
    public EvolutionServiceImpl(TyphonQLInterface tql, TyphonMLInterface tml, TyphonDLInterface tdl){
        this.typhonDLInterface = tdl;
        this.typhonMLInterface = tml;
        this.typhonQLInterface = tql;


        entityHandlers = new EnumMap<>(EvolutionOperator.class);

        entityHandlers.put(EvolutionOperator.ADD, new EntityAddHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.REMOVE, new EntityRemoveHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.RENAME, new EntityRenameHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.MIGRATE, new EntityMigrateHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.SPLITHORIZONTAL, new EntitySplitHorizontalHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.SPLITVERTICAL,  new EntitySplitVerticalHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.MERGE,  new EntityMergeHandler(tdl, tml, tql));



        relationHandlers = new EnumMap<>(EvolutionOperator.class);

        relationHandlers.put(EvolutionOperator.ADD, new RelationAddHandler(tdl, tml, tql));
        relationHandlers.put(EvolutionOperator.REMOVE, new RelationRemoveHandler(tdl, tml, tql));
        relationHandlers.put(EvolutionOperator.ENABLECONTAINMENT, new RelationEnableContainmentHandler(tdl, tml, tql));
        relationHandlers.put(EvolutionOperator.DISABLECONTAINMENT, new RelationDisableContainmentHandler(tdl, tml, tql));
    }

    @Override
    public Model evolveEntity(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported{
        Handler operation = entityHandlers.get(smo.getEvolutionOperator());
        return execute_handlers(operation, smo, model);
    }

    @Override
    public Model evolveRelation(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported{
        Handler operation = relationHandlers.get(smo.getEvolutionOperator());
        return execute_handlers(operation, smo, model);
    }

    private Model execute_handlers(Handler handler, SMO smo, Model model)throws InputParameterException, EvolutionOperationNotSupported{
        if(handler != null){
            return handler.handle(smo, model);
        }

        String err_msg = String.format("No operation found for [%s] - [%s]", smo.getTyphonObject(), smo.getEvolutionOperator());
        throw new EvolutionOperationNotSupported(err_msg);
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
