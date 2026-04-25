package com.document.verification.service.globalExceptionHandling.customException;

public class S3DownloadException extends RuntimeException {
  public S3DownloadException(String message) {
    super(message);
  }
}
