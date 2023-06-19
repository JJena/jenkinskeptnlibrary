package com.test.keptn.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;

public class jsonBeautify {
    public static String jsonBeautify(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            Object json = objectMapper.readValue(jsonString, Object.class);
            String beautifiedJson = objectWriter.writeValueAsString(json);
            return beautifiedJson;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
