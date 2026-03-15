package com.educationloan.document.globalExceptionHandling.CustomException;

public class S3PresignedUrlException extends RuntimeException {
  public S3PresignedUrlException(String message) {
    super(message);
  }
}
