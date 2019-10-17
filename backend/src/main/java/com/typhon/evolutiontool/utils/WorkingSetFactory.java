package main.java.com.typhon.evolutiontool.utils;

import main.java.com.typhon.evolutiontool.dummy.WorkingSetDummyImpl;
import main.java.com.typhon.evolutiontool.entities.WorkingSet;

public class WorkingSetFactory {

    public static WorkingSet createEmptyWorkingSet() {
        return new WorkingSetDummyImpl();
    }
}
