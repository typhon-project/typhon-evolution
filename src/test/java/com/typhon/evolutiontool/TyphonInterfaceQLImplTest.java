package com.typhon.evolutiontool;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLConnection;
import com.typhon.evolutiontool.services.typhonQL.TyphonInterfaceQLImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class TyphonInterfaceQLImplTest {

    TyphonQLConnection typhonQLConnection;
    TyphonInterfaceQLImpl typhonQLGenerator=new TyphonInterfaceQLImpl();

    @Test
    public void testTyphonQLCreateEntity(){
        Entity entity;
        entity = new Entity("FakeEntity");
        entity.addAttribute("fakeAttribute", "string");
        entity.addAttribute("fake2", "date");
//        when(typhonQLConnection.executeTyphonQLDDL(anyString())).thenReturn("dummy");
        assertEquals("TyphonQL CREATE ENTITY FakeEntity {fake2 date,fakeAttribute string}", typhonQLGenerator.createEntityType(entity,"fakeVersion"));
    }

}