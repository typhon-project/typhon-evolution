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
import com.typhon.evolutiontool.handlers.index.AddCollectionIndexHandler;
import com.typhon.evolutiontool.handlers.index.AddTableIndexHandler;
import com.typhon.evolutiontool.handlers.relation.*;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterfaceImpl;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import it.univaq.disim.typhon.acceleo.services.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Model;

import java.io.*;
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
    private Map<EvolutionOperator, Handler> indexHandlers;


    public EvolutionServiceImpl() {
        this.typhonDLInterface = new TyphonDLInterfaceImpl();
        this.typhonMLInterface = new TyphonMLInterfaceImpl();
        this.typhonQLInterface = new TyphonQLInterfaceImpl();

        entityHandlers = new EnumMap<>(EvolutionOperator.class);
        entityHandlers.put(EvolutionOperator.ADD, new EntityAddHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.REMOVE, new EntityRemoveHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.RENAME, new EntityRenameHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.MIGRATE, new EntityMigrateHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.SPLITHORIZONTAL, new EntitySplitHorizontalHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.SPLITVERTICAL, new EntitySplitVerticalHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
//        entityHandlers.put(EvolutionOperator.MERGE, new EntityMergeHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        entityHandlers.put(EvolutionOperator.MERGE, new EntityNewMergeHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));

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

        indexHandlers = new EnumMap<>(EvolutionOperator.class);
        indexHandlers.put(EvolutionOperator.ADD_TABLE_INDEX, new AddTableIndexHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
        indexHandlers.put(EvolutionOperator.ADD_COLLECTION_INDEX, new AddCollectionIndexHandler(typhonDLInterface, typhonMLInterface, typhonQLInterface));
    }

    @Override
    public Model prepareEvolution(String changeOperatorsFilePath) throws Exception {
        try {
            //1. Retrieve the latest XMI version of the deployed model
            Model currentModel = typhonQLInterface.getCurrentModel();
            //2. Serialize the XMI model in a TML model
            Services.serializeTML(currentModel, "temp/currentTMLModel.tml");
            //3. Read TML model and change operators files as Strings
            String tmlContent;
            String changeOperators;
            try {
                BufferedReader tmlReader = new BufferedReader(new FileReader("temp/currentTMLModel.tml"));
                StringBuilder tmlReaderStringBuilder = new StringBuilder();
                String line;
                while ((line = tmlReader.readLine()) != null) {
                    tmlReaderStringBuilder.append(line);
                }
                tmlReader.close();
                tmlContent = tmlReaderStringBuilder.toString();
                BufferedReader changeOperatorsReader = new BufferedReader(new FileReader(changeOperatorsFilePath));
                StringBuilder changeOperatorsStringBuilder = new StringBuilder();
                while ((line = changeOperatorsReader.readLine()) != null) {
                    changeOperatorsStringBuilder.append(line);
                }
                changeOperatorsReader.close();
                changeOperators = changeOperatorsStringBuilder.toString();
            } catch (IOException e) {
                logger.error("Error while reading current model and change operators files. Aborting evolution...");
                throw new Exception("Error while reading current model and change operators files. Aborting evolution...");
            }
            logger.debug("Model content: " + tmlContent);
            logger.debug("Change operators to apply: " + changeOperators);
            //4. Create the new TML model containing the current model and the change operators
            try (PrintWriter out = new PrintWriter("temp/newTMLModel.tml")) {
                out.println(tmlContent);
                out.println(changeOperators);
            } catch (FileNotFoundException e) {
                logger.error("Error while creating the TML model file containing the model and the change operators");
                throw new Exception("Error while creating the TML model file containing the model and the change operators");
            }
            //5. Load the new TML model
            Model newModel = Services.loadXtextModel("temp/newTMLModel.tml");
            //6. Save the new XMI model in a file
            Services.serialize(newModel, "temp/newXMIModel.xmi");
            return newModel;
        } catch (Exception e) {
            throw new Exception("Error while preparing the model for evolution");
        }
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

    @Override
    public Model evolveIndex(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {
        Handler operation = indexHandlers.get(smo.getEvolutionOperator());
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
