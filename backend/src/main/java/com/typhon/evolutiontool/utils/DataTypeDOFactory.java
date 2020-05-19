package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.DataTypeDO;
import com.typhon.evolutiontool.entities.DataTypeDOImpl;
import typhonml.DataType;

public class DataTypeDOFactory {

    private DataTypeDOFactory() {
    }

    public static DataTypeDO buildInstance(DataType dataType) {
        if (dataType != null) {
            //TODO: deprecated getNewType? see SMOAdapter
//            return new DataTypeDOImpl(dataType.getName(), dataType.getImportedNamespace());
            return new DataTypeDOImpl("StringType");
        }
        return null;
    }
}
