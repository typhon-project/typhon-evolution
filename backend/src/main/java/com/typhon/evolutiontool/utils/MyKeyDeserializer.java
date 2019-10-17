package main.java.com.typhon.evolutiontool.utils;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

public class MyKeyDeserializer extends KeyDeserializer {
    @Override
    public String deserializeKey(String s, DeserializationContext deserializationContext) throws IOException {
        return s.toLowerCase();
    }
}
