package com.cece.scoring.controller;


import com.cece.scoring.request.ScoringClientRequest;
import com.cece.scoring.response.ScoringClientResponse;
import com.cece.scoring.response.ScoringResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/v1/scoring")
@RequiredArgsConstructor
@Slf4j
public class MockScoringController {

    private final Map<String,String> customerScoreTokes = new HashMap<>();
    private final RestTemplate restTemplate;
    private final Random random = new Random();
    private final List<String> randomUUIDs = Arrays.asList(
            "550e8400-e29b-41d4-a716-446655440000", "550e8400-e29b-41d4-a716-446655440001",
            "550e8400-e29b-41d4-a716-446655440002", "550e8400-e29b-41d4-a716-446655440003",
            "550e8400-e29b-41d4-a716-446655440004", "550e8400-e29b-41d4-a716-446655440005",
            "550e8400-e29b-41d4-a716-446655440006", "550e8400-e29b-41d4-a716-446655440007",
            "550e8400-e29b-41d4-a716-446655440008", "550e8400-e29b-41d4-a716-446655440009",
            "550e8400-e29b-41d4-a716-446655440010", "550e8400-e29b-41d4-a716-446655440011",
            "550e8400-e29b-41d4-a716-446655440012", "550e8400-e29b-41d4-a716-446655440013",
            "550e8400-e29b-41d4-a716-446655440014", "550e8400-e29b-41d4-a716-446655440015",
            "550e8400-e29b-41d4-a716-446655440016", "550e8400-e29b-41d4-a716-446655440017",
            "550e8400-e29b-41d4-a716-446655440018", "550e8400-e29b-41d4-a716-446655440019"
    );

    @Value("${api.scoring.transactions.url}")
    private String transactionsUrl;


    @GetMapping("/initiateQueryScore/{customerNumber}")
    public ResponseEntity<String> initiateQueryScore(@PathVariable String customerNumber,
                                                     @RequestHeader("client-token") String clientToken) {
        log.info("Initiating query score for customer number: {}", customerNumber);
        if(!randomUUIDs.contains(clientToken)) {
            log.info("Initiating query score for customer number {} failed: Invalid client-token {}", customerNumber, clientToken);
            throw new RuntimeException("Invalid client-token");
        }

        String uuid = UUID.randomUUID().toString();
        customerScoreTokes.put(uuid, customerNumber);

        return ResponseEntity.ok(uuid);
    }

    @GetMapping("/queryScore/{token}")
    public ResponseEntity<ScoringResponse> queryScore(@PathVariable String token,
                                                      @RequestHeader("client-token") String clientToken)
            throws InterruptedException {
        log.info("Querying score for token: {}", token);
        if(!randomUUIDs.contains(clientToken)) {
            log.info("Querying score failed: Invalid client-token {}", clientToken);
            throw new RuntimeException("Invalid client-token");
        }

        if(!customerScoreTokes.containsKey(token)) {
            log.info("Querying score failed: Invalid token {}", token);
            throw new RuntimeException("Invalid token");
        }

        //Timeout 20% of the time
        if(random.nextInt(100)<20) {
            Thread.sleep(60000);
            return ResponseEntity.internalServerError().build();
        }


        String customerNumber = customerScoreTokes.get(token);
        customerScoreTokes.remove(token);

        String customerTransactions = getCustomerTransactions(customerNumber);
        double div = random.nextDouble() * 2.5 + 1;
        int score = (int)(1000/div);
        double limit = customerTransactions.length() / div;

        ScoringResponse response = new ScoringResponse();
        response.setId(random.nextLong());
        response.setCustomerNumber(customerNumber);
        response.setScore(score);
        response.setLimitAmount(limit);
        response.setExclusion("No Exclusion");
        response.setExclusionReason("None");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/client/createClient")
    public ResponseEntity<ScoringClientResponse> createClient(@RequestBody ScoringClientRequest request) {
        int tokenId = random.nextInt(randomUUIDs.size());
        ScoringClientResponse response = new ScoringClientResponse();
        response.setId(1L);
        response.setUrl(request.getUrl());
        response.setName(request.getName());
        response.setUsername(request.getUsername());
        response.setPassword(request.getPassword());
        response.setToken(randomUUIDs.get(tokenId));
        return ResponseEntity.ok(response);
    }


    private String getCustomerTransactions(String customerNumber) {
        String url = String.format("%s?customerNumber=%s",transactionsUrl, customerNumber);
        log.info("Getting transactions for customer: {}", url);
        return restTemplate.getForObject(url, String.class);
    }


}
