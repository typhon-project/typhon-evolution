package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository <Message,Long> {

}
