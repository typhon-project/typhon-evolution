package com.typhon.evolutiontool.dao;

import com.typhon.evolutiontool.entities.TyphonMLSchema;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TyphonMongoRepository extends MongoRepository<TyphonMLSchema, String> {

    public TyphonMLSchema findByVersion(String version);

}
