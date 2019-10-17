package main.java.com.typhon.evolutiontool.entities;

import java.util.List;

public class DocumentDB extends Database {

    private List<Collection> collections;

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }
}
