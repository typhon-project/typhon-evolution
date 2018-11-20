package com.typhon.evolutiontool;

import com.typhon.evolutiontool.dummy.DataAccessAPIConnectionDummy;
import com.typhon.evolutiontool.services.TyphonDataAccessAPIServiceImpl;
import com.typhon.evolutiontool.services.StructureChange;
import com.typhon.evolutiontool.services.TyphonDataAccessAPIConnection;
import com.typhon.evolutiontool.services.TyphonDataAccessAPIService;
import org.junit.Test;

import static junit.framework.TestCase.fail;


public class TyphonDataAccessAPITests {

    private TyphonDataAccessAPIConnection dataAccessAPIConnection = new DataAccessAPIConnectionDummy();
    private TyphonDataAccessAPIService dataAccessAPI = new TyphonDataAccessAPIServiceImpl(dataAccessAPIConnection);

    @Test
    public void testExecuteQueryThroughDataAccessAPI() {
        try {
            dataAccessAPIConnection.executeQuery("Fake Query");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testExecuteStructureChange(){
        StructureChange structureChange = new StructureChange("StructureChange Fake query");
        try {
            dataAccessAPI.executeStructureChange(structureChange);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }



}
