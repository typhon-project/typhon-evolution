package com.typhon.evolutiontool.services;


import com.typhon.evolutiontool.entities.SMOAdapter;
import typhonml.ChangeOperator;

public class SMOFactory {

    public static SMOAdapter createSMOAdapterFromChangeOperator(ChangeOperator changeOperator) {
        return new SMOAdapter(changeOperator);
    }
}
