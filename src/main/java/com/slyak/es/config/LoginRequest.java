package com.slyak.es.config;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;

    private String password;
}
