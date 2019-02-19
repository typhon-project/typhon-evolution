package com.typhon.evolutiontool.services;

public interface TyphonQLConnection {
    TyphonQLConnection getTyphonQLConnectionOnSpecifiedTyphonML(String typhonMLversion);

    String executeTyphonQLDDL(String tqlDDL);
}
