package com.example.aviationapiwrapper.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricsService {
    private final MeterRegistry meterRegistry;

    public Timer.Sample startSample() {
        return Timer.start(meterRegistry);
    }

    public void recordExternalCall(Timer.Sample s, String service, String result) {
        s.stop(Timer.builder("external.call.duration").description("Time taken for calls to external services")
                    .tag("service", service).tag("result", result).publishPercentiles(0.5, 0.95, 0.99)
                    .publishPercentileHistogram(true).register(meterRegistry));
    }

    public void recordOperation(Timer.Sample s, String op, String result) {
        s.stop(Timer.builder("operation.duration").description("End-to-end operation duration")
                    .tag("operation", op).tag("result", result).publishPercentiles(0.5, 0.95, 0.99)
                    .publishPercentileHistogram(true).register(meterRegistry));
    }
}