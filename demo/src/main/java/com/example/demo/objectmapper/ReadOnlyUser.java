package com.example.demo.objectmapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReadOnlyUser {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id; // 읽기 전용 필드

    private String username;

    private String email;

}
