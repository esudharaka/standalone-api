package com.programming.utills;

import java.io.IOException;

public class MappingUtils {

    public  static  <T> T toObject(String jsonString, Class<T> t) throws IOException {
        final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return objectMapper.readValue(jsonString, t);
    }

    public static   String toJsonString(Object obj) throws IOException {
        final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
