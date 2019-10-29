package main.java.com.typhon.evolutiontool.utils;

import main.java.com.typhon.evolutiontool.entities.DataTypeDO;
import main.java.com.typhon.evolutiontool.entities.DataTypeDOImpl;
import typhonml.DataType;

public class DataTypeDOFactory {

    private DataTypeDOFactory() {
    }

    public static DataTypeDO buildInstance(DataType dataType) {
        if (dataType != null) {
            return new DataTypeDOImpl(dataType.getName(), dataType.getImportedNamespace());
        }
        return null;
    }
}
