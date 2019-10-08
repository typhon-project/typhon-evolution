package com.typhon.evolutiontool.entities;

public class AttributeDOImpl implements AttributeDO {

    private String name;
    private String importedNamespace;
    private String dataTypeName;
    private String dataTypeImportedNamespace;

    public AttributeDOImpl(String name, String importedNamespace, String dataTypeName, String dataTypeImportedNamespace) {
        this.name = name;
        this.importedNamespace = importedNamespace;
        this.dataTypeName = dataTypeName;
        this.dataTypeImportedNamespace = dataTypeImportedNamespace;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImportedNamespace() {
        return importedNamespace;
    }

    @Override
    public String getDataTypeName() {
        return dataTypeName;
    }

    @Override
    public String getDataTypeImportedNamespace() {
        return dataTypeImportedNamespace;
    }
}
