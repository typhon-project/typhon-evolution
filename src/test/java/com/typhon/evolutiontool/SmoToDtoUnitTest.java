package com.typhon.evolutiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.SMODto;
import com.typhon.evolutiontool.entities.TyphonMLObject;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class SmoToDtoUnitTest {

    private ObjectMapper mapper = new ObjectMapper();
    private ModelMapper modelMapper = new ModelMapper();
    private final String ADD_ENTITY_JSON = "{"+
            "\"smo\":{"+
            "\"typhonObject\":\"Entity\","+
            "\"evolutionOperator\":\"Add\","+
            "\"input\":{"+
            "\"entity\":\"Professor\","+
            "\"attributes\":{"+
            "\"name\":\"string\","+
            "\"hireDate\":\"date\""+
            "},"+
            "\"databasetype\":\"relationaldb\","+
            "\"databasemappingname\":\"Professor\","+
            "\"id\":\"name\""+
            "}"
            +"}"
            +"}";

    @Test
    public void SmoToDto() throws IOException {
        SMO smo = new SMO(TyphonMLObject.ENTITY, EvolutionOperator.ADD);
        smo.setInputParameter(mapper.readTree(ADD_ENTITY_JSON));

        SMODto smoDto = modelMapper.map(smo, SMODto.class);
        assertEquals(smo.getEvolutionOperator(), smoDto.getEvolutionOperator());
        assertEquals(smo.getTyphonObject(), smoDto.getTyphonObject());
        assertEquals(smo.getInputParameter(), smoDto.getInputParameter());
    }

    @Test
    public void DtoToSMO() throws IOException {
        SMODto smoDto = new SMODto(TyphonMLObject.ENTITY, EvolutionOperator.ADD, mapper.readTree(ADD_ENTITY_JSON));
        SMO smo = modelMapper.map(smoDto, SMO.class);
        assertEquals(smo.getEvolutionOperator(), smoDto.getEvolutionOperator());
        assertEquals(smo.getTyphonObject(), smoDto.getTyphonObject());
        assertEquals(smo.getInputParameter(), smoDto.getInputParameter());
    }
}
