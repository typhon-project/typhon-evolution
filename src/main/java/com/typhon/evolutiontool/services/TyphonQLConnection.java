package com.typhon.evolutiontool.services;

public interface TyphonQLConnection {
    String executeTyphonQLDDL(String tqlDDL, String typhonMLversion);
}
