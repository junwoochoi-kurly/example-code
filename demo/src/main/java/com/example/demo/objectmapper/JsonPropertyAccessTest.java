package com.example.demo.objectmapper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonPropertyAccessTest {

    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. 직렬화 테스트 (Object -> JSON)
        TestClass testObject = new TestClass();
        testObject.setReadOnlyField("This is read-only");
        testObject.setNormalField("This is normal");

        String jsonResult = objectMapper.writeValueAsString(testObject);
        System.out.println("Serialized JSON: " + jsonResult);

        // 2. 역직렬화 테스트 (JSON -> Object)
        String inputJson = "{ \"readOnlyField\": \"New value\", \"normalField\": \"Updated normal\" }";
        TestClass deserializedObject = objectMapper.readValue(inputJson, TestClass.class);

        System.out.println("Deserialized Object:");
        System.out.println("ReadOnlyField: " + deserializedObject.getReadOnlyField()); // 예상: null
        System.out.println("NormalField: " + deserializedObject.getNormalField());     // 예상: Updated normal
    }

}
