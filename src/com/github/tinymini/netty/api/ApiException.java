package com.github.tinymini.netty.api;

import com.github.tinymini.netty.common.exception.CustomException;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * API 익셉션
 * 
 * @author shkim
 *
 */
public class ApiException extends CustomException {
  private static final long serialVersionUID = -744741081235948125L;

  public ApiException(String message) {
    super(INTERNAL_SERVER_ERROR, message);
  }

  public ApiException(HttpResponseStatus status, String message) {
    super(status, message);
  }

  public ApiException(HttpResponseStatus status, Throwable cause) {
    super(status, cause);
  }

  public ApiException(Throwable cause) {
    super(INTERNAL_SERVER_ERROR, cause);
  }
}
