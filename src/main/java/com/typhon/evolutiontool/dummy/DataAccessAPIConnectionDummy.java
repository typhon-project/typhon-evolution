package com.typhon.evolutiontool.dummy;

import com.typhon.evolutiontool.services.TyphonDataAccessAPIConnection;
import org.springframework.stereotype.Component;

@Component
public class DataAccessAPIConnectionDummy implements TyphonDataAccessAPIConnection {
    @Override
    public void executeQuery(String tqlQuery) {
        System.out.println("Executing : ["+tqlQuery+" ]");
    }
}
