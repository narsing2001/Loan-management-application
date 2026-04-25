package com.document.verification.service.globalExceptionHandling.customException;

public class InvalidFileException extends RuntimeException {
  public InvalidFileException(String message) {
    super(message);
  }
}
