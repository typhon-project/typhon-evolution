package com.typhon.evolutiontool;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.services.TyphonQLConnection;
import com.typhon.evolutiontool.services.TyphonQLInterfaceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TyphonQLInterfaceImplTest {

    @Mock
    TyphonQLConnection typhonQLConnection;
    @InjectMocks
    TyphonQLInterfaceImpl typhonQLGenerator=new TyphonQLInterfaceImpl();

    @Test
    public void testTyphonQLCreateEntity(){
        Entity entity;
        entity = new Entity("FakeEntity");
        entity.addAttribute("fakeAttribute", "string");
        entity.addAttribute("fake2", "date");
        when(typhonQLConnection.executeTyphonQLDDL(anyString(),anyString())).thenReturn("dummy");
        assertEquals("TyphonQL CREATE ENTITY FakeEntity {fake2 date,fakeAttribute string}", typhonQLGenerator.createEntity(entity,"fakeVersion"));
    }

}