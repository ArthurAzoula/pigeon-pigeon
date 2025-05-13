package com.pigeonpigeon.pigeon.configuration.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@Data
public class ErrorDetails {

    private LocalDate date;
    private HttpStatus status;
    private String details;

    public ErrorDetails(LocalDate date, HttpStatus status, String details) {
        this.date = date;
        this.status = status;
        this.details = details;
    }
}
