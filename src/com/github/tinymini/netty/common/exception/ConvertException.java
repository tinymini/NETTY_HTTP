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

  public ConvertException(int errorCode, Class<?> source, Class<?> destinationType) {
    super(errorCode, "Fail to convert : ", source.getName(), " to ", destinationType.getName());
  }

  public ConvertException(Class<?> source, Class<?> destinationType) {
    this(Code.CASTING_FAIL, source, destinationType);
  }
}
