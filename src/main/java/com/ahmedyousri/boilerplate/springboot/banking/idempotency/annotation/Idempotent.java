package com.ahmedyousri.boilerplate.springboot.banking.idempotency.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark controller methods as idempotent.
 * Methods annotated with @Idempotent will be processed by the IdempotencyAspect
 * to ensure they are only executed once for a given idempotency key.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    
    /**
     * The header name to use for the idempotency key.
     * Default is "Idempotency-Key".
     */
    String headerName() default "Idempotency-Key";
    
    /**
     * Whether the idempotency key is required.
     * If true, requests without an idempotency key will be rejected.
     * Default is true.
     */
    boolean required() default true;
    
    /**
     * The number of days after which the idempotency key expires.
     * Default is 7 days.
     */
    int expirationDays() default 7;
}
