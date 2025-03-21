package com.cece.scoring.request;

import lombok.Data;

@Data
public class ScoringClientRequest {
    private String url;
    private String name;
    private String username;
    private String password;
}
