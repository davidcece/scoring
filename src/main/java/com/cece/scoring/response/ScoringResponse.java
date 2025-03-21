package com.cece.scoring.response;

import lombok.Data;

@Data
public class ScoringResponse {
    private Long id;
    private String customerNumber;
    private int score;
    private double limitAmount;
    private String exclusion;
    private String exclusionReason;
}
