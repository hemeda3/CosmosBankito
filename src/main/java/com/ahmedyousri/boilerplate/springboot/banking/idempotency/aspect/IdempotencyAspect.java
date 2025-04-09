package com.ahmedyousri.boilerplate.springboot.banking.idempotency.aspect;

import com.ahmedyousri.boilerplate.springboot.banking.exception.BusinessRuleException;
import com.ahmedyousri.boilerplate.springboot.banking.idempotency.annotation.Idempotent;
import com.ahmedyousri.boilerplate.springboot.banking.idempotency.entity.IdempotencyKey;
import com.ahmedyousri.boilerplate.springboot.banking.idempotency.service.IdempotencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Aspect that handles idempotency for methods annotated with @Idempotent.
 * This aspect intercepts method calls and ensures they are only executed once
 * for a given idempotency key.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyAspect.class);
    
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;
    
    /**
     * Around advice for methods annotated with @Idempotent.
     * This method checks if the request has already been processed based on the idempotency key.
     * If it has, it returns the stored response. Otherwise, it proceeds with the method execution.
     * 
     * @param joinPoint The join point representing the intercepted method call
     * @return The result of the method execution or the stored response
     * @throws Throwable If an error occurs during method execution
     */
    @Around("@annotation(com.ahmedyousri.boilerplate.springboot.banking.idempotency.annotation.Idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get the method signature and annotation
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent idempotentAnnotation = method.getAnnotation(Idempotent.class);
        
        // Get the current HTTP request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // Not a web request, just proceed
            return joinPoint.proceed();
        }
        
        HttpServletRequest request = attributes.getRequest();
        String requestPath = request.getRequestURI();
        
        // Get the idempotency key from the request header
        String idempotencyKey = request.getHeader(idempotentAnnotation.headerName());
        
        // Check if the idempotency key is required but missing
        if (idempotentAnnotation.required() && (idempotencyKey == null || idempotencyKey.isEmpty())) {
            throw new BusinessRuleException(
                    "Idempotency key is required for this operation", 
                    "MISSING_IDEMPOTENCY_KEY");
        }
        
        // If no idempotency key is provided and it's not required, just proceed
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            return joinPoint.proceed();
        }
        
        // Check for stored response
        Optional<IdempotencyKey> storedKey = idempotencyService.getStoredResponse(idempotencyKey, requestPath);
        if (storedKey.isPresent() && storedKey.get().getResponseData() != null) {
            // Return the stored response for duplicate requests
            log.info("Returning stored response for idempotency key: {}", idempotencyKey);
            
            // If the stored response is a ResponseEntity, try to reconstruct it
            String responseData = storedKey.get().getResponseData();
            Integer statusCode = storedKey.get().getStatusCode();
            
            if (method.getReturnType().equals(ResponseEntity.class)) {
                try {
                    // Parse the stored response data
                    Map<String, Object> responseBody = objectMapper.readValue(responseData, HashMap.class);
                    
                    // Add a flag to indicate this is a duplicate request
                    responseBody.put("isDuplicate", true);
                    
                    // Create a new ResponseEntity with the stored status code
                    return ResponseEntity
                            .status(statusCode != null ? statusCode : HttpStatus.OK.value())
                            .body(responseBody);
                } catch (Exception e) {
                    log.warn("Failed to parse stored response data: {}", e.getMessage());
                    // Fall back to proceeding with the method
                }
            }
        }
        
        // Check if this is a new request
        boolean isIdempotent = idempotencyService.isIdempotent(idempotencyKey, requestPath);
        if (!isIdempotent) {
            // This is a duplicate request but we don't have a stored response yet
            // This can happen if the original request is still processing
            log.warn("Duplicate request detected but no stored response found for key: {}", idempotencyKey);
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Duplicate request detected, but original request is still processing");
            responseBody.put("isDuplicate", true);
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
        }
        
        // Proceed with the method execution
        Object result = joinPoint.proceed();
        
        // Store the response for future duplicate requests
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            
            // Store the response body and status code
            idempotencyService.storeResponse(
                    idempotencyKey,
                    requestPath,
                    objectMapper.writeValueAsString(responseEntity.getBody()),
                    responseEntity.getStatusCode().value()
            );
        }
        
        return result;
    }
}
