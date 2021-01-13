package com.typhon.evolutiontool.datatypes;

import typhonml.DataType;
import typhonml.StringType;

public class StringTypeDO implements DataTypeDO {

    private static final int MAX_SIZE_DEFAULT = 255;
    private int maxSize;

    public StringTypeDO(DataType type) {
        int maxSize = ((StringType)type).getMaxSize();
        this.maxSize = maxSize != 0 ? maxSize : MAX_SIZE_DEFAULT;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
