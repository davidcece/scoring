package com.cece.scoring.response;

import lombok.Data;

@Data
public class ScoringClientResponse {
    private Long id;
    private String url;
    private String name;
    private String username;
    private String password;
    private String token;
}