package main.java.com.typhon.evolutiontool.services;

import main.java.com.typhon.evolutiontool.entities.EvolutionOperator;
import main.java.com.typhon.evolutiontool.entities.SMO;
import main.java.com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import main.java.com.typhon.evolutiontool.handlers.Handler;
import main.java.com.typhon.evolutiontool.handlers.attribute.AttributeAddHandler;
import main.java.com.typhon.evolutiontool.handlers.attribute.AttributeChangeTypeHandler;
import main.java.com.typhon.evolutiontool.handlers.attribute.AttributeRemoveHandler;
import main.java.com.typhon.evolutiontool.handlers.attribute.AttributeRenameHandler;
import main.java.com.typhon.evolutiontool.handlers.entity.*;
import main.java.com.typhon.evolutiontool.handlers.relation.*;
import main.java.com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import main.java.com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
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

    Handler entityRename;
    private Model targetModel;


    private Map<EvolutionOperator, Handler> entityHandlers;
    private Map<EvolutionOperator, Handler> relationHandlers;
    private Map<EvolutionOperator, Handler> attributeHandlers;


    public EvolutionServiceImpl(TyphonQLInterface tql, TyphonMLInterface tml, TyphonDLInterface tdl) {
        this.typhonDLInterface = tdl;
        this.typhonMLInterface = tml;
        this.typhonQLInterface = tql;

        entityHandlers = new EnumMap<>(EvolutionOperator.class);
        entityHandlers.put(EvolutionOperator.ADD, new EntityAddHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.REMOVE, new EntityRemoveHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.RENAME, new EntityRenameHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.MIGRATE, new EntityNewMigrateHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.SPLITHORIZONTAL, new EntitySplitHorizontalHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.SPLITVERTICAL, new EntitySplitVerticalHandler(tdl, tml, tql));
        entityHandlers.put(EvolutionOperator.MERGE, new EntityMergeHandler(tdl, tml, tql));

        relationHandlers = new EnumMap<>(EvolutionOperator.class);
        relationHandlers.put(EvolutionOperator.ADD, new RelationAddHandler(tdl, tml, tql));
        relationHandlers.put(EvolutionOperator.REMOVE, new RelationRemoveHandler(tdl, tml, tql));
        relationHandlers.put(EvolutionOperator.ENABLECONTAINMENT, new RelationEnableContainmentHandler(tdl, tml, tql));
        relationHandlers.put(EvolutionOperator.DISABLECONTAINMENT, new RelationDisableContainmentHandler(tdl, tml, tql));
        relationHandlers.put(EvolutionOperator.ENABLEOPPOSITE, new RelationEnableOppositeHandler(tdl, tml, tql));
        relationHandlers.put(EvolutionOperator.DISABLEOPPOSITE, new RelationDisableOppositeHandler(tdl, tml, tql));
        relationHandlers.put(EvolutionOperator.RENAME, new RelationRenameHandler(tdl, tml, tql));
        relationHandlers.put(EvolutionOperator.CHANGECARDINALITY, new RelationChangeCardinalityHandler(tdl, tml, tql));

        attributeHandlers = new EnumMap<>(EvolutionOperator.class);
        attributeHandlers.put(EvolutionOperator.ADD, new AttributeAddHandler(tdl, tml, tql));
        attributeHandlers.put(EvolutionOperator.REMOVE, new AttributeRemoveHandler(tdl, tml, tql));
        attributeHandlers.put(EvolutionOperator.RENAME, new AttributeRenameHandler(tdl, tml, tql));
        attributeHandlers.put(EvolutionOperator.CHANGETYPE, new AttributeChangeTypeHandler(tdl, tml, tql));
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


    public boolean containParameters(SMO smo, List<String> parameters) {
        logger.info("Verifying input parameter for [{}] - [{}] operator", smo.getTyphonObject(), smo.getEvolutionOperator());
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
