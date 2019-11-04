package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.ChangeOperatorParameter;
import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.Handler;
import com.typhon.evolutiontool.handlers.attribute.AttributeAddHandler;
import com.typhon.evolutiontool.handlers.attribute.AttributeChangeTypeHandler;
import com.typhon.evolutiontool.handlers.attribute.AttributeRemoveHandler;
import com.typhon.evolutiontool.handlers.attribute.AttributeRenameHandler;
import com.typhon.evolutiontool.handlers.entity.*;
import com.typhon.evolutiontool.handlers.relation.*;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonQL.TyphonInterfaceQLImpl;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/*
    This class implements the operations needed to complete the execution of a Schema Modification Operator (SMO).
    For each operator it will :
     1. Verify that the needed parameters are in the input parameter map of the SMO object.
     2. Execute the structure and data change operations of the SMO operator by calling the TyphonQLInterface object implementation
     3. Modify the TyphonML module that the operator is executed an that the current running TyphonML model can be changed.
 */
public class EvolutionServiceImpl implements EvolutionService {

    private Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);

    private TyphonDLInterface typhonDLInterface;
    private TyphonQLInterface typhonQLInterface;
    private TyphonMLInterface typhonMLInterface;

    private Map<EvolutionOperator, Handler> entityHandlers;
    private Map<EvolutionOperator, Handler> relationHandlers;
    private Map<EvolutionOperator, Handler> attributeHandlers;


    public EvolutionServiceImpl() {
        this.typhonDLInterface = new TyphonDLInterfaceImpl();
        this.typhonMLInterface = new TyphonMLInterfaceImpl();
        this.typhonQLInterface = new TyphonInterfaceQLImpl();

        entityHandlers = new EnumMap<>(EvolutionOperator.class);
        entityHandlers.put(EvolutionOperator.ADD, new EntityAddHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.REMOVE, new EntityRemoveHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.RENAME, new EntityRenameHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.MIGRATE, new EntityMigrateHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.SPLITHORIZONTAL, new EntitySplitHorizontalHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.SPLITVERTICAL, new EntitySplitVerticalHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.MERGE, new EntityMergeHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));

        relationHandlers = new EnumMap<>(EvolutionOperator.class);
        relationHandlers.put(EvolutionOperator.ADD, new RelationAddHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        relationHandlers.put(EvolutionOperator.REMOVE, new RelationRemoveHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        relationHandlers.put(EvolutionOperator.ENABLECONTAINMENT, new RelationEnableContainmentHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        relationHandlers.put(EvolutionOperator.DISABLECONTAINMENT, new RelationDisableContainmentHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        relationHandlers.put(EvolutionOperator.CHANGECONTAINMENT, new RelationChangeContainmentHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        relationHandlers.put(EvolutionOperator.ENABLEOPPOSITE, new RelationEnableOppositeHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        relationHandlers.put(EvolutionOperator.DISABLEOPPOSITE, new RelationDisableOppositeHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        relationHandlers.put(EvolutionOperator.RENAME, new RelationRenameHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        relationHandlers.put(EvolutionOperator.CHANGECARDINALITY, new RelationChangeCardinalityHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));

        attributeHandlers = new EnumMap<>(EvolutionOperator.class);
        attributeHandlers.put(EvolutionOperator.ADD, new AttributeAddHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        attributeHandlers.put(EvolutionOperator.REMOVE, new AttributeRemoveHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        attributeHandlers.put(EvolutionOperator.RENAME, new AttributeRenameHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        attributeHandlers.put(EvolutionOperator.CHANGETYPE, new AttributeChangeTypeHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
    }

    @Override
    public Model evolveEntity(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {
        Handler operation = entityHandlers.get(smo.getEvolutionOperator());
        return executeHandlers(operation, smo, model);
    }

    @Override
    public Model evolveRelation(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {
        Handler operation = relationHandlers.get(smo.getEvolutionOperator());
        return executeHandlers(operation, smo, model);
    }

    @Override
    public Model evolveAttribute(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {
        Handler operation = attributeHandlers.get(smo.getEvolutionOperator());
        return executeHandlers(operation, smo, model);
    }

    private Model executeHandlers(Handler handler, SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {
        if (handler != null) {
            return handler.handle(smo, model);
        }

        String err_msg = String.format("No operation found for [%s] - [%s]", smo.getTyphonObject(), smo.getEvolutionOperator());
        throw new EvolutionOperationNotSupported(err_msg);
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


    public boolean containParameters(SMO smo, List<ChangeOperatorParameter> parameters) {
        logger.info("Verifying input parameter for [{}] - [{}] operator", smo.getTyphonObject(), smo.getEvolutionOperator());
        return smo.inputParametersContainsExpected(parameters);
    }

    public TyphonDLInterface getTyphonDLInterface() {
        return typhonDLInterface;
    }

    public TyphonQLInterface getTyphonQLInterface() {
        return typhonQLInterface;
    }

    public TyphonMLInterface getTyphonMLInterface() {
        return typhonMLInterface;
    }
}
