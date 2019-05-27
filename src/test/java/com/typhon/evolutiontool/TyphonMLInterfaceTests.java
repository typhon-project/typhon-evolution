package com.typhon.evolutiontool;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterfaceImpl;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import typhonml.Attribute;
import typhonml.Model;
import typhonml.TyphonmlFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class TyphonMLInterfaceTests {

    TyphonMLInterface typhonMLInterface = new TyphonMLInterfaceImpl();
    public static Model sourceModel, targetModel;
    public static final String sourcemodelpath = "resources/baseModel.xmi";

    @BeforeClass
    public static void setUp() {
        TyphonMLUtils.typhonMLPackageRegistering();
        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
    }

    @Test
    public void testCreateEntityTyphonML() throws IOException, InputParameterException {
        Entity entity = new Entity("fakeEntity");
        targetModel = typhonMLInterface.createEntityType(sourceModel,entity);
        assertNotNull(targetModel);
        assertEquals(entity.getName(),targetModel.getDataTypes().get(0).getName());
    }

    @Test
    public void testCreateEntityWithAttributesTyphonML() throws IOException, InputParameterException {
        Entity entity = new Entity("fakeEntity");
        entity.addAttribute("attr1","string");
        entity.addAttribute("attr2", "number");
        targetModel = typhonMLInterface.createEntityType(sourceModel,entity);
        assertNotNull(targetModel);
        typhonml.Entity retrievedEntity = (typhonml.Entity) targetModel.getDataTypes().get(0);
        assertEquals(entity.getName(),targetModel.getDataTypes().get(0).getName());
        assertEquals("attr1", retrievedEntity.getAttributes().get(1).getName());
        assertEquals("attr2", retrievedEntity.getAttributes().get(0).getName());
    }
}
