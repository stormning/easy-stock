package com.slyak.es.bean;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;

    private String password;
}
