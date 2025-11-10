package com.example.aviationapiwrapper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ProblemDetail> onClient(WebClientResponseException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null || !status.is4xxClientError()) {
            status = HttpStatus.BAD_GATEWAY;
        }
        String detail = StringUtils.hasText(ex.getResponseBodyAsString()) ? ex.getResponseBodyAsString()
                                                                          : "Upstream error: "
                                                                            + ex.getStatusCode().value();
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(status.is4xxClientError() ? "Upstream resource error" : "Upstream provider error");
        return Mono.just(pd);
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ProblemDetail> onAny(Throwable ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        pd.setTitle("Internal error");
        return Mono.just(pd);
    }
}
