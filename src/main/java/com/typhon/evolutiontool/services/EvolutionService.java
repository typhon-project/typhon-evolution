package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.SMO;

public interface EvolutionService {
    String addEntity(SMO smo);

    String renameEntity(SMO smo);
}
