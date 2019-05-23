package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.DocumentDB;
import com.typhon.evolutiontool.entities.RelationalDB;
import com.typhon.evolutiontool.entities.TyphonMLSchema;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import static junit.framework.TestCase.*;

public class TyphonMLModelTest {

    private ObjectMapper mapper = new ObjectMapper();
    private TyphonMLSchema schema;

    @Test
    public void testReadTyphonML() throws IOException {
        schema = mapper.readerFor(TyphonMLSchema.class).readValue(new File("src/main/resources/test/TyphonML_V2.json"));
        assertEquals("TyphonML_V2",schema.getVersion());
        assertTrue(schema.getDatabases().get(0) instanceof RelationalDB);
        assertTrue(schema.getDatabases().get(1) instanceof DocumentDB);
        assertEquals("client",schema.getEntities().get(0).getName());
        assertEquals("string", schema.getEntities().get(0).getAttributes().get("name"));
    }
}
