package main.java.com.typhon.evolutiontool.entities;


import java.util.Objects;

public class TyphonDatabase {

    private String databasetype;

    private String databasename;

    public TyphonDatabase(String databasetype, String databasename) {
        this.databasetype =databasetype;
        this.databasename = databasename;
    }

    public TyphonDatabase() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TyphonDatabase that = (TyphonDatabase) o;
        return Objects.equals(databasetype, that.databasetype) &&
                Objects.equals(databasename, that.databasename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(databasetype, databasename);
    }

    public String getDatabasetype() {
        return databasetype;
    }

    public void setDatabasetype(String databasetype) {
        this.databasetype = databasetype;
    }

    public String getDatabasename() {
        return databasename;
    }

    public void setDatabasename(String databasename) {
        this.databasename = databasename;
    }

    @Override
    public String toString() {
        return "TyphonDatabase{" +
                "databasetype='" + databasetype + '\'' +
                ", databasename='" + databasename + '\'' +
                '}';
    }
}
