package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;

public interface TyphonInterface {

    String createEntity(Entity newEntity);

    void setTyphonMLTargetModel(String targetmodel);

    void renameEntity(String oldEntityName, String newEntityName);
}
