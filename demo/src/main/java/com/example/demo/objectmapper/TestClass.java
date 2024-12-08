package com.example.demo.objectmapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestClass {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String readOnlyField;

    private String normalField;

}
