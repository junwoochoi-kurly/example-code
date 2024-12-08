package com.example.demo.objectmapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WirteOnlyUser {

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String email;

}
