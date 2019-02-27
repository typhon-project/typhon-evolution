package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import java.util.List;

public class TyphonDLSchema {

    @Id
    String id;

    @JsonProperty("runningdb")
    private List<TyphonDatabase> runningdb;

}
