package main.java.com.typhon.evolutiontool.entities;

import java.util.Map;

public interface EntityDO {

    void addAttribute(String name, String datatype);

    String getName();

    void setName(String name);

    String getIdentifier();

    Map<String, Object> getAttributes();
}
