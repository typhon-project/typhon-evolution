package main.java.com.typhon.evolutiontool.utils;


import main.java.com.typhon.evolutiontool.entities.SMOAdapter;
import typhonml.ChangeOperator;

public class SMOFactory {

    public static SMOAdapter createSMOAdapterFromChangeOperator(ChangeOperator changeOperator) {
        return new SMOAdapter(changeOperator);
    }
}
