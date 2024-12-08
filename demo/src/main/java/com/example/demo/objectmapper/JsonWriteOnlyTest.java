package com.example.demo.objectmapper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonWriteOnlyTest {

    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. JSON -> 객체 변환 (역직렬화)
        String inputJson = "{ \"username\": \"junwoo_choi\", \"password\": \"junwoo_choi123\", \"email\": \"junwoo_choi@example.com\" }";
        WirteOnlyUser wirteOnlyUser = objectMapper.readValue(inputJson, WirteOnlyUser.class);

        System.out.println("Deserialized User:");
        System.out.println("Username: " + wirteOnlyUser.getUsername());
        System.out.println("Password: " + wirteOnlyUser.getPassword());
        System.out.println("Email: " + wirteOnlyUser.getEmail());

        // 2. 객체 -> JSON 변환 (직렬화)
        String outputJson = objectMapper.writeValueAsString(wirteOnlyUser);
        System.out.println("\nSerialized JSON:");
        System.out.println(outputJson);
    }

}
