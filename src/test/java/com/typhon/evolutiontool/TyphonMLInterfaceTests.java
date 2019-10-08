package com.typhon.evolutiontool;

import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.EntityDOJsonImpl;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterfaceImpl;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import typhonml.Model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TyphonMLInterfaceTests {

    private TyphonMLInterface typhonMLInterface = new TyphonMLInterfaceImpl();
    private static Model sourceModel, targetModel;
    private static final String sourcemodelpath = "resources/baseModel.xmi";

    @BeforeClass
    public static void setUp() {
        TyphonMLUtils.typhonMLPackageRegistering();
        sourceModel = TyphonMLUtils.loadModelTyphonML(sourcemodelpath);
    }

    @Test
    public void testCreateEntityTyphonML() {
        EntityDO entity = new EntityDOJsonImpl("fakeEntity");
        targetModel = typhonMLInterface.createEntityType(sourceModel, entity);
        assertNotNull(targetModel);
        assertEquals(entity.getName(), targetModel.getDataTypes().get(0).getName());
    }

    @Test
    public void testCreateEntityWithAttributesTyphonML() {
        EntityDO entity = new EntityDOJsonImpl("fakeEntity");
        entity.addAttribute("attr1", "string");
        entity.addAttribute("attr2", "number");
        targetModel = typhonMLInterface.createEntityType(sourceModel, entity);
        assertNotNull(targetModel);
        typhonml.Entity retrievedEntity = (typhonml.Entity) targetModel.getDataTypes().get(0);
        assertEquals(entity.getName(), targetModel.getDataTypes().get(0).getName());
        assertEquals("attr1", retrievedEntity.getAttributes().get(1).getName());
        assertEquals("attr2", retrievedEntity.getAttributes().get(0).getName());
    }
}
