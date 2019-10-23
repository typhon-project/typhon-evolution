package com.typhon.evolutiontool.test;

import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.EntityDOImpl;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterfaceImpl;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import typhonml.Model;

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
        EntityDO entity = new EntityDOImpl("fakeEntity", null, null, null, null);
        targetModel = typhonMLInterface.createEntityType(sourceModel, entity);
        Assert.assertNotNull(targetModel);
        Assert.assertEquals(entity.getName(), targetModel.getDataTypes().get(0).getName());
    }

    @Test
    public void testCreateEntityWithAttributesTyphonML() {
        EntityDO entity = new EntityDOImpl("fakeEntity", null, null, null, null);
        entity.addAttribute("attr1", "string");
        entity.addAttribute("attr2", "number");
        targetModel = typhonMLInterface.createEntityType(sourceModel, entity);
        Assert.assertNotNull(targetModel);
        typhonml.Entity retrievedEntity = (typhonml.Entity) targetModel.getDataTypes().get(0);
        Assert.assertEquals(entity.getName(), targetModel.getDataTypes().get(0).getName());
        Assert.assertEquals("attr1", retrievedEntity.getAttributes().get(1).getName());
        Assert.assertEquals("attr2", retrievedEntity.getAttributes().get(0).getName());
    }
}
