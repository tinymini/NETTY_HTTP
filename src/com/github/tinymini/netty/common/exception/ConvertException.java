package com.github.tinymini.netty.common.exception;

import com.github.tinymini.netty.common.Code;

/**
 * 형 변환 익셉션
 * 
 * @author shkim
 *
 */
public class ConvertException extends CustomException {
  private static final long serialVersionUID = 446304056906294909L;
  private static final String NULL = "NULL";
  
  public ConvertException(int errorCode, Object source, Class<?> destinationType) {
    super(errorCode, "Fail to convert : ", source == null ? NULL : source.getClass().getName(),
        " to ", destinationType == null ? NULL : destinationType.getName());
  }

  public ConvertException(Object source, Class<?> destinationType) {
    this(Code.CASTING_FAIL, source, destinationType);
  }
}
