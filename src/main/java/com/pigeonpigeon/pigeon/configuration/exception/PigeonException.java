package com.pigeonpigeon.pigeon.configuration.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PigeonException extends RuntimeException {

  private HttpStatus httpStatus;

  private String details;
}
