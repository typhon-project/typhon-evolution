package com.typhon.evolutiontool.utils;

import java.util.List;
import java.util.UUID;

public class UUIDGenerator {

    public static String buildUUID(String entityName, List<String> params) {
        if (params == null || params.size() == 0) {
            return UUID.randomUUID().toString();
        }

        StringBuilder source = new StringBuilder(entityName + "//");
        for (String param : params) {
            source.append(":").append(param);
        }
        byte[] bytes = source.toString().getBytes();
        UUID uuid = UUID.nameUUIDFromBytes(bytes);
        return uuid.toString();
    }

}
