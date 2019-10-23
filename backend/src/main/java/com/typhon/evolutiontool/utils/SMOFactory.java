package com.typhon.evolutiontool.utils;


import com.typhon.evolutiontool.entities.SMOAdapter;

import typhonml.ChangeOperator;

public class SMOFactory {

    public static SMOAdapter createSMOAdapterFromChangeOperator(ChangeOperator changeOperator) {
        return new SMOAdapter(changeOperator);
    }
}
