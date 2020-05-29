package com.typhon.evolutiontool.entities;

public class DataTypeDOImpl implements DataTypeDO {

    private String name;

    public DataTypeDOImpl(String name) {
        this.name = name;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
