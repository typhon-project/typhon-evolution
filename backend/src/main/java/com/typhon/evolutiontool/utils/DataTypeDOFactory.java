package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.datatypes.*;
import typhonml.*;

public class DataTypeDOFactory {

    private DataTypeDOFactory() {
    }

    public static DataTypeDO buildInstance(DataType dataType) {
        if (dataType != null) {
            if (dataType instanceof BigintType) {
                return new BigIntTypeDO();
            }
            if (dataType instanceof BlobType) {
                return new BlobTypeDO();
            }
            if (dataType instanceof BoolType) {
                return new BoolTypeDO();
            }
            if (dataType instanceof DatetimeType) {
                return new DatetimeTypeDO();
            }
            if (dataType instanceof DateType) {
                return new DateTypeDO();
            }
            if (dataType instanceof FloatType) {
                return new FloatTypeDO();
            }
            if (dataType instanceof FreetextType) {
                return new FreetextTypeDO();
            }
            if (dataType instanceof IntType) {
                return new IntTypeDO();
            }
            if (dataType instanceof PointType) {
                return new PointTypeDO();
            }
            if (dataType instanceof PolygonType) {
                return new PolygonTypeDO();
            }
            if (dataType instanceof StringType) {
                return new StringTypeDO(dataType);
            }
            if (dataType instanceof TextType) {
                return new TextTypeDO();
            }
        }
        return null;
    }

    public static DataTypeDO buildInstance(CustomDataType dataType) {
        if (dataType != null) {
            return new CustomTypeDO(dataType);
        }
        return null;
    }
}
