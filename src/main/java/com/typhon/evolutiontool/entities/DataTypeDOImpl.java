package com.typhon.evolutiontool.entities;

public class DataTypeDOImpl implements DataTypeDO {

    private String name;
    private String importedNamespace;

    public DataTypeDOImpl(String name, String importedNamespace) {
        this.name = name;
        this.importedNamespace = importedNamespace;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getImportedNamespace() {
        return this.importedNamespace;
    }

    @Override
    public void setImportedNamespace(String importedNamespace) {
        this.importedNamespace = importedNamespace;

    }
}
