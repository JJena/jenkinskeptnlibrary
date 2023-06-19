package com.test.keptn.utils;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class extractJsonField {
    public static Object extractFieldFromResponse(String responseBody, String fieldPath) {
        // Parse the JSON response and extract the desired field
        // Modify this code according to your JSON structure
        // This is just a simple example using the org.json library
        try {
            Object fieldValue = JsonPath.read(responseBody, fieldPath);
            return fieldValue;
        } catch (PathNotFoundException e) {
            System.out.println(fieldPath + ": Field not found");
        }
        return null;
    }
}
