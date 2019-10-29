package main.java.com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class TyphonDLSchema {

    String version;

    @JsonProperty("runningdb")
    private List<TyphonDatabase> runningdb;

    @Override
    public boolean equals(Object o) {
        boolean compare = false;
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TyphonDLSchema that = (TyphonDLSchema) o;
        compare = this.runningdb.containsAll(that.runningdb) && that.runningdb.containsAll(this.runningdb);
        return compare;
    }

    @Override
    public int hashCode() {
        return Objects.hash(runningdb);
    }

    public TyphonDLSchema() {
    }

    @Override
    public String toString() {
        return "TyphonDLSchema{" +
                "version='" + version + '\'' +
                ", runningdb=" + runningdb +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<TyphonDatabase> getRunningdb() {
        return runningdb;
    }

    public void setRunningdb(List<TyphonDatabase> runningdb) {
        this.runningdb = runningdb;
    }
}
