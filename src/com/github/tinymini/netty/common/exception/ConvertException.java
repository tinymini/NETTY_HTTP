package com.github.tinymini.netty.common.exception;

import com.github.tinymini.netty.common.HttpCode;
import com.github.tinymini.netty.common.util.MessageUtils;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 형 변환 익셉션
 * 
 * @author shkim
 *
 */
public class ConvertException extends CustomException implements HttpCode {
  private static final long serialVersionUID = 446304056906294909L;
  private static final String FAIL_CONVERT = "FAIL_CONVERT";



  public ConvertException(HttpResponseStatus status, Object source, Class<?> destinationType) {
    super(status,
        MessageUtils.getMessage(MESSAGE_BUNDLE, FAIL_CONVERT,
            source == null ? NULL : source.getClass().getName(),
            destinationType == null ? NULL : destinationType.getName()));
  }

  public ConvertException(Object source, Class<?> destinationType) {
    this(INTERNAL_SERVER_ERROR, source, destinationType);
  }
}
