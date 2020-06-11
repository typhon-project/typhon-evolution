package com.typhon.evolutiontool.handlers.index;

import com.typhon.evolutiontool.datatypes.DataTypeDO;
import com.typhon.evolutiontool.entities.ChangeOperatorParameter;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.StorageUnitDO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.StorageUnitDOFactory;
import typhonml.Attribute;
import typhonml.Collection;
import typhonml.Model;
import typhonml.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddCollectionIndexHandler extends BaseHandler {

    public AddCollectionIndexHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.COLLECTION, ChangeOperatorParameter.ATTRIBUTES))) {
            StorageUnitDO storageUnitDO = StorageUnitDOFactory.buildInstance((Collection) smo.getInputParameter().get(ChangeOperatorParameter.COLLECTION));
            Map<String, DataTypeDO> entityAttributes = buildEntityAttributes((List<Attribute>) smo.getInputParameter().get(ChangeOperatorParameter.ATTRIBUTES));

            //TyphonML
            String databaseName = typhonMLInterface.getEntityDatabase(storageUnitDO.getEntity().getName(), model).getName();
            Model targetModel = typhonMLInterface.addCollectionIndex(databaseName, storageUnitDO.getName(), storageUnitDO.getEntity().getName(), entityAttributes.keySet(), model);
            targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

            //TyphonQL
            //TODO: upload the schema and add the index when QL has implemented the change operator
            //typhonQLInterface.uploadSchema(targetModel);
//            typhonQLInterface.addCollectionIndex(databaseName, storageUnitDO.getName(), storageUnitDO.getEntity().getName(), entityAttributes.keySet());

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter. Needed [" + ChangeOperatorParameter.ATTRIBUTE + "]");
        }
    }
}
