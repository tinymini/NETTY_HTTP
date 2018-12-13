package com.github.tinymini.netty.api;

import com.github.tinymini.netty.common.Code;
import com.github.tinymini.netty.common.exception.CustomException;

/**
 * API 익셉션
 * 
 * @author shkim
 *
 */
public class ApiException extends CustomException {
  private static final long serialVersionUID = -744741081235948125L;

  public ApiException(String message) {
    super(Code.API_NOT_EXIST, message);
  }

  public ApiException(int errorCode, String message) {
    super(errorCode, message);
  }

  public ApiException(int errorCode, Throwable cause) {
    super(Code.UNKNOWN_ERROR, cause);
  }

  public ApiException(Throwable cause) {
    super(Code.UNKNOWN_ERROR, cause);
  }
}
