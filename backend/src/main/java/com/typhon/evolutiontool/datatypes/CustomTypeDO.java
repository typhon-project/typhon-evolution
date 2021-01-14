package com.typhon.evolutiontool.datatypes;

import typhonml.CustomDataType;
import typhonml.SuperDataType;

import java.util.List;

public class CustomTypeDO implements DataTypeDO {
    private List<SuperDataType> elements;

    public CustomTypeDO(CustomDataType dataType) {
        this.elements = dataType.getElements();
    }

    public List<SuperDataType> getElements() {
        return elements;
    }

    public void setElements(List<SuperDataType> elements) {
        this.elements = elements;
    }
}
