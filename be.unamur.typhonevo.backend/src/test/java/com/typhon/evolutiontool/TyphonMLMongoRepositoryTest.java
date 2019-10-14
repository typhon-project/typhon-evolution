package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.dao.TyphonMongoRepository;
import com.typhon.evolutiontool.entities.TyphonMLSchema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataMongoTest
// Line below is to run with the real MongoDB server
//@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class TyphonMLMongoRepositoryTest {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    TyphonMongoRepository typhonMongoRepository;

    private String collectionName;
    private ObjectMapper mapper = new ObjectMapper();
    private TyphonMLSchema schema;

    @Before
    public void initializeMongo() throws IOException {
        collectionName = "TyphonML";
        schema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V2.json"));
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
        assertNotNull(typhonMongoRepository.findByVersion(savedSchema.getVersion()));
    }
}
