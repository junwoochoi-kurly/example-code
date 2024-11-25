package com.example.demo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObjectMapperTest {

    @Test
    void test() {
        Long a = null;
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Model1(a);
        });
    }
    record Model1(long a) {}


    @Test
    public void testNullInjectionIntoPrimitiveField() {
        // Given
        String jsonWithNull = "{\"num\":null}";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);

        // When & Then
        assertThrows(MismatchedInputException.class, () -> {
            objectMapper.readValue(jsonWithNull, TestObject.class);
        });
    }
    static class TestObject {
        public int num; // primitive type
    }

}
