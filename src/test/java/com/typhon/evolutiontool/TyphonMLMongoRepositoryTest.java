package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBCollection;
import com.typhon.evolutiontool.dao.TyphonMongoRepository;
import com.typhon.evolutiontool.entities.TyphonMLSchema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataMongoTest
public class TyphonMLMongoRepositoryTest {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    TyphonMongoRepository typhonMongoRepository;
    String collectionName;
    ObjectMapper mapper = new ObjectMapper();
    TyphonMLSchema schema;

    @Before
    public void initializeMongo() throws IOException {
        collectionName="TyphonML";
        schema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/FakeTyphonML.json"));
        mongoTemplate.save(schema);
    }

    @After
    public void after() {
        mongoTemplate.dropCollection(collectionName);
    }

    @Test
    public void checkMongoTemplate() {
        assertNotNull(mongoTemplate);
        mongoTemplate.createCollection(collectionName);
        assertTrue(mongoTemplate.collectionExists(collectionName));
    }

    @Test
    public void checkTyphonMongoRepository() {
        assertNotNull(typhonMongoRepository);
        TyphonMLSchema savedSchema = typhonMongoRepository.save(schema);
        assertNotNull(typhonMongoRepository.findByVersion("TyphonML_V2"));
    }

}
