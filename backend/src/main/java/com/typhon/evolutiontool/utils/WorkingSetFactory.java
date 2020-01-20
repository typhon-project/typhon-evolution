package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.WorkingSetImpl;
import com.typhon.evolutiontool.entities.WorkingSet;

public class WorkingSetFactory {

    public static WorkingSet createEmptyWorkingSet() {
        return new WorkingSetImpl();
    }
}
