package com.example.demo.objectmapper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReadOnlyTest {

    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. JSON -> 객체 변환 (역직렬화)
        String inputJson = "{\"username\": \"john_doe\", \"email\": \"john@example.com\" }";
        ReadOnlyUser user = objectMapper.readValue(inputJson, ReadOnlyUser.class);

        System.out.println("Deserialized User:");
        System.out.println("ID: " + user.getId());          // 예상: null (무시됨)
        System.out.println("Username: " + user.getUsername());  // john_doe
        System.out.println("Email: " + user.getEmail());        // john@example.com

        // 2. 객체 -> JSON 변환 (직렬화)
        user.setId("67890"); // 읽기 전용 필드 설정
        String outputJson = objectMapper.writeValueAsString(user);

        System.out.println("\nSerialized JSON:");
        System.out.println(outputJson);
    }

}
