package com.typhon.evolutiontool.entities;

public enum ChangeOperatorParameter {

    DATABASE("database"),

    ENTITY("entity"),

    NEW_ENTITY_NAME("newEntityName"),

    ENTITY_NAME("entityName"),

    ATTRIBUTE_VALUE("attributeValue"),

    RELATION("relation"),

    RELATION_NAME("relationName"),

    FIRST_NEW_ENTITY("firstNewEntity"),

    SECOND_NEW_ENTITY("secondNewEntity"),

    CARDINALITY("cardinality"),

    ATTRIBUTE("attribute"),

    ATTRIBUTE_NAME("attributeName"),

    ATTRIBUTE_TYPE("attributeType"),

    NEW_ATTRIBUTE_NAME("newAttributeName"),

    NEW_CONTAINMENT("newContainment"),

    FIRST_ENTITY_TO_MERGE("firstEntityToMerge"),

    SECOND_ENTITY_TO_MERGE("secondEntityToMerge");

    private String parameterKey;

    ChangeOperatorParameter(String parameterKey) {
        this.parameterKey = parameterKey;
    }

    public String getParameterKey() {
        return parameterKey;
    }
}
