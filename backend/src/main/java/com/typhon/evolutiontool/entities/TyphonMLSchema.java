package main.java.com.typhon.evolutiontool.entities;

import java.util.List;
import java.util.Objects;

public class TyphonMLSchema {

    private String version;
    private List<Database> databases;
    private List<EntityDO> entities;

    public TyphonMLSchema() {
    }

    public TyphonMLSchema(String typhonMLVersion) {
        this.version = typhonMLVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Database> databases) {
        this.databases = databases;
    }

    public List<EntityDO> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityDO> entities) {
        this.entities = entities;
    }

    public EntityDO getEntityFromName(String entityid) {
        return entities.stream().filter(e -> e.getName().equals(entityid)).findAny().orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TyphonMLSchema that = (TyphonMLSchema) o;
        return Objects.equals(version, that.version) &&
                databases.containsAll(that.databases) &&
                that.databases.containsAll(this.databases) &&
                entities.containsAll(that.entities) &&
                that.entities.containsAll(this.entities);
    }

    @Override
    public String toString() {
        return "TyphonMLSchema{" +
                "version='" + version + '\'' +
                ", databases=" + databases +
                ", entities=" + entities +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, databases, entities);
    }
}
