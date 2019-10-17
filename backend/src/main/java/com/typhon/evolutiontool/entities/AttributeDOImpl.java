package main.java.com.typhon.evolutiontool.entities;

public class AttributeDOImpl implements AttributeDO {

    private String name;
    private String importedNamespace;
    private DataTypeDO dataTypeDO;

    public AttributeDOImpl(String name, String importedNamespace, DataTypeDO dataTypeDO) {
        this.name = name;
        this.importedNamespace = importedNamespace;
        this.dataTypeDO = dataTypeDO;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getImportedNamespace() {
        return this.importedNamespace;
    }

    @Override
    public DataTypeDO getDataTypeDO() {
        return this.dataTypeDO;
    }
}
