package com.ahmedyousri.boilerplate.springboot.banking.monitoring.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Service for monitoring system performance and operations.
 * This service provides methods for recording metrics and sending alerts.
 */
@Service
@RequiredArgsConstructor
public class MonitoringService {
    
    private static final Logger log = LoggerFactory.getLogger(MonitoringService.class);
    
    private final MeterRegistry meterRegistry;
    private final AlertService alertService;
    
    @Value("${app.monitoring.enabled:true}")
    private boolean monitoringEnabled;
    
    @Value("${app.monitoring.latency.threshold.warning:1000}")
    private long latencyThresholdWarningMs;
    
    @Value("${app.monitoring.latency.threshold.error:5000}")
    private long latencyThresholdErrorMs;
    
    @Value("${app.monitoring.latency.threshold.critical:10000}")
    private long latencyThresholdCriticalMs;
    
    // Cache for timers to avoid creating new ones for each operation
    private final Map<String, Timer> timers = new ConcurrentHashMap<>();
    
    // Cache for counters to avoid creating new ones for each operation
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    
    /**
     * Record the latency of an operation.
     *
     * @param operationType  The type of operation
     * @param durationMillis The duration of the operation in milliseconds
     */
    public void recordOperationLatency(String operationType, long durationMillis) {
        if (!monitoringEnabled) {
            return;
        }
        
        log.debug("Recording latency for operation {}: {} ms", operationType, durationMillis);
        
        // Record the latency in the metrics registry
        Timer timer = getOrCreateTimer(operationType);
        timer.record(durationMillis, TimeUnit.MILLISECONDS);
        
        // Check if the latency exceeds thresholds and send alerts if necessary
        if (durationMillis > latencyThresholdCriticalMs) {
            alertService.sendAlert(
                    "Critical latency for operation " + operationType + ": " + durationMillis + " ms",
                    AlertService.AlertLevel.CRITICAL
            );
        } else if (durationMillis > latencyThresholdErrorMs) {
            alertService.sendAlert(
                    "High latency for operation " + operationType + ": " + durationMillis + " ms",
                    AlertService.AlertLevel.ERROR
            );
        } else if (durationMillis > latencyThresholdWarningMs) {
            alertService.sendAlert(
                    "Elevated latency for operation " + operationType + ": " + durationMillis + " ms",
                    AlertService.AlertLevel.WARNING
            );
        }
    }
    
    /**
     * Record a failed operation.
     *
     * @param operationType The type of operation
     * @param e             The exception that caused the failure
     */
    public void recordFailedOperation(String operationType, Exception e) {
        if (!monitoringEnabled) {
            return;
        }
        
        log.debug("Recording failed operation {}: {}", operationType, e.getMessage());
        
        // Increment the failure counter
        Counter counter = getOrCreateCounter(operationType + ".failure");
        counter.increment();
        
        // Send an alert
        alertService.sendAlert(
                "Failed operation: " + operationType + " - " + e.getMessage(),
                AlertService.AlertLevel.ERROR
        );
    }
    
    /**
     * Record a successful operation.
     *
     * @param operationType The type of operation
     */
    public void recordSuccessfulOperation(String operationType) {
        if (!monitoringEnabled) {
            return;
        }
        
        log.debug("Recording successful operation {}", operationType);
        
        // Increment the success counter
        Counter counter = getOrCreateCounter(operationType + ".success");
        counter.increment();
    }
    
    /**
     * Time an operation and record its latency.
     *
     * @param operationType The type of operation
     * @return A timer context that can be stopped to record the latency
     */
    public Timer.Sample timeOperation(String operationType) {
        if (!monitoringEnabled) {
            return null;
        }
        
        log.debug("Starting timer for operation {}", operationType);
        
        return Timer.start(meterRegistry);
    }
    
    /**
     * Stop a timer and record the latency.
     *
     * @param sample        The timer sample
     * @param operationType The type of operation
     */
    public void stopTimer(Timer.Sample sample, String operationType) {
        if (!monitoringEnabled || sample == null) {
            return;
        }
        
        log.debug("Stopping timer for operation {}", operationType);
        
        Timer timer = getOrCreateTimer(operationType);
        long durationNanos = sample.stop(timer);
        long durationMillis = TimeUnit.NANOSECONDS.toMillis(durationNanos);
        
        // Check if the latency exceeds thresholds and send alerts if necessary
        if (durationMillis > latencyThresholdCriticalMs) {
            alertService.sendAlert(
                    "Critical latency for operation " + operationType + ": " + durationMillis + " ms",
                    AlertService.AlertLevel.CRITICAL
            );
        } else if (durationMillis > latencyThresholdErrorMs) {
            alertService.sendAlert(
                    "High latency for operation " + operationType + ": " + durationMillis + " ms",
                    AlertService.AlertLevel.ERROR
            );
        } else if (durationMillis > latencyThresholdWarningMs) {
            alertService.sendAlert(
                    "Elevated latency for operation " + operationType + ": " + durationMillis + " ms",
                    AlertService.AlertLevel.WARNING
            );
        }
    }
    
    /**
     * Record a gauge value.
     *
     * @param name  The name of the gauge
     * @param value The value of the gauge
     */
    public void recordGaugeValue(String name, double value) {
        if (!monitoringEnabled) {
            return;
        }
        
        log.debug("Recording gauge value for {}: {}", name, value);
        
        meterRegistry.gauge(name, value);
    }
    
    /**
     * Get or create a timer for an operation type.
     *
     * @param operationType The type of operation
     * @return The timer
     */
    private Timer getOrCreateTimer(String operationType) {
        return timers.computeIfAbsent(operationType, key -> {
            log.debug("Creating timer for operation type: {}", key);
            return Timer.builder("operation.latency")
                    .tag("operation", key)
                    .description("Latency of " + key + " operations")
                    .publishPercentiles(0.5, 0.95, 0.99)
                    .publishPercentileHistogram()
                    .minimumExpectedValue(Duration.ofMillis(1))
                    .maximumExpectedValue(Duration.ofSeconds(30))
                    .register(meterRegistry);
        });
    }
    
    /**
     * Get or create a counter for an operation type.
     *
     * @param name The name of the counter
     * @return The counter
     */
    private Counter getOrCreateCounter(String name) {
        return counters.computeIfAbsent(name, key -> {
            log.debug("Creating counter for: {}", key);
            return Counter.builder("operation.count")
                    .tag("operation", key)
                    .description("Count of " + key + " operations")
                    .register(meterRegistry);
        });
    }
}
