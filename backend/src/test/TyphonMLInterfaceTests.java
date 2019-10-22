package test;

import main.java.com.typhon.evolutiontool.entities.EntityDO;
import main.java.com.typhon.evolutiontool.entities.EntityDOJsonImpl;
import main.java.com.typhon.evolutiontool.entities.EvolutionOperator;
import main.java.com.typhon.evolutiontool.entities.ParametersKeyString;
import main.java.com.typhon.evolutiontool.entities.RelationDO;
import main.java.com.typhon.evolutiontool.entities.SMOAdapter;
import main.java.com.typhon.evolutiontool.entities.SMOJsonImpl;
import main.java.com.typhon.evolutiontool.entities.TyphonMLObject;
import main.java.com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterfaceImpl;
import main.java.com.typhon.evolutiontool.utils.SMOFactory;
import main.java.com.typhon.evolutiontool.utils.TyphonMLUtils;
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
