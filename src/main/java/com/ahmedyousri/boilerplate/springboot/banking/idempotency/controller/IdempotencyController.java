package com.ahmedyousri.boilerplate.springboot.banking.idempotency.controller;

import com.ahmedyousri.boilerplate.springboot.banking.idempotency.annotation.Idempotent;
import com.ahmedyousri.boilerplate.springboot.banking.idempotency.entity.IdempotencyKey;
import com.ahmedyousri.boilerplate.springboot.banking.idempotency.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller demonstrating the use of idempotency keys.
 * This controller provides endpoints that show how to use the IdempotencyService
 * to prevent duplicate request processing.
 */
@RestController
@RequestMapping("/api/v1/idempotency")
@RequiredArgsConstructor
public class IdempotencyController {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyController.class);
    
    private final IdempotencyService idempotencyService;
    
    /**
     * Example endpoint that demonstrates idempotency key usage with the @Idempotent annotation.
     * The same request with the same idempotency key will return the same response.
     * 
     * @param idempotencyKey The idempotency key header
     * @return A response entity with a message
     */
    @PostMapping("/example")
    @Idempotent(headerName = "Idempotency-Key", required = true)
    public ResponseEntity<Map<String, Object>> exampleEndpoint(
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey) {
        
        // Process the request (this is where your actual business logic would go)
        // The @Idempotent annotation handles all the idempotency logic through the aspect
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Request processed successfully");
        responseBody.put("timestamp", System.currentTimeMillis());
        responseBody.put("idempotencyKey", idempotencyKey);
        
        return ResponseEntity.ok(responseBody);
    }
    
    /**
     * Endpoint to check the status of an idempotency key.
     * 
     * @param idempotencyKey The idempotency key to check
     * @param request The HTTP request
     * @return Information about the idempotency key
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkIdempotencyKey(
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey,
            HttpServletRequest request) {
        
        String requestPath = request.getParameter("path");
        if (requestPath == null) {
            requestPath = "/api/v1/idempotency/example"; // Default path
        }
        
        Optional<IdempotencyKey> storedKey = idempotencyService.getStoredResponse(idempotencyKey, requestPath);
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("idempotencyKey", idempotencyKey);
        responseBody.put("path", requestPath);
        
        if (storedKey.isPresent()) {
            responseBody.put("exists", true);
            responseBody.put("createdAt", storedKey.get().getCreatedAt());
            responseBody.put("hasStoredResponse", storedKey.get().getResponseData() != null);
        } else {
            responseBody.put("exists", false);
        }
        
        return ResponseEntity.ok(responseBody);
    }
}
