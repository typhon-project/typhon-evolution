package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;

public class TyphonQLGeneratorImplTest {

    @InjectMocks
    TyphonQLGeneratorImpl typhonQLGenerator=new TyphonQLGeneratorImpl();

    @Test
    public void testTyphonQLCreateEntity(){
        Entity entity;
        entity = new Entity("FakeEntity");
        entity.addAttribute("fakeAttribute", "string");
        entity.addAttribute("fake2", "date");
        assertEquals("TyphonQL CREATE ENTITY FakeEntity {fake2 date,fakeAttribute string}", typhonQLGenerator.createEntity(entity));
    }

}