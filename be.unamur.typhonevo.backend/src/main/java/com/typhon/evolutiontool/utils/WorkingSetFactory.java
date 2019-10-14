package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.dummy.WorkingSetDummyImpl;
import com.typhon.evolutiontool.entities.WorkingSet;

public class WorkingSetFactory {

    public static WorkingSet createEmptyWorkingSet() {
        return new WorkingSetDummyImpl();
    }
}
