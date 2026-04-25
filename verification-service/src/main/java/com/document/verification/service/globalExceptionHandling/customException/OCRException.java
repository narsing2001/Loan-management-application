package com.document.verification.service.globalExceptionHandling.customException;

public class OCRException extends RuntimeException {
  public OCRException(String message, Exception e) {
    super(message);
  }
}
