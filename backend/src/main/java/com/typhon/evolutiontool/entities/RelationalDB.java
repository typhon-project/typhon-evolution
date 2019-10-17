package main.java.com.typhon.evolutiontool.entities;

import java.util.List;

public class RelationalDB extends Database {

    private List<Table> tables;

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
}
