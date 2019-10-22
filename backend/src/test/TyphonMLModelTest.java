package test;

import com.fasterxml.jackson.databind.ObjectMapper;

import main.java.com.typhon.evolutiontool.entities.DocumentDB;
import main.java.com.typhon.evolutiontool.entities.RelationalDB;
import main.java.com.typhon.evolutiontool.entities.TyphonMLSchema;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TyphonMLModelTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testReadTyphonML() throws IOException {
        TyphonMLSchema schema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V2.json"));
        assertEquals("TyphonML_V2", schema.getVersion());
        assertTrue(schema.getDatabases().get(0) instanceof RelationalDB);
        assertTrue(schema.getDatabases().get(1) instanceof DocumentDB);
        assertEquals("client", schema.getEntities().get(0).getName());
        assertEquals("string", schema.getEntities().get(0).getAttributes().get("name"));
    }
}
