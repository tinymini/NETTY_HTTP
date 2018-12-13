package com.github.tinymini.netty.common.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.tinymini.netty.common.Code;

/**
 * 메세지 유틸 포함 익셉션
 * 
 * @author shkim
 *
 */
public class CustomException extends RuntimeException {
  private static final long serialVersionUID = 7420063159681514892L;
  protected final Log logger = LogFactory.getLog(getClass());
  protected int errorCode = Code.FAIL;

  protected CustomException(int errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  protected CustomException(int errorCode, String... messages) {
    this(errorCode, makeMessage(messages));
  }

  protected CustomException(int errorCode, Throwable cause) {
    this(errorCode, cause.getMessage());

    if (logger.isInfoEnabled()) {
      StackTraceElement[] stack = cause.getStackTrace();
      if (stack.length > 2) {
        logger.info(makeMessage(stack[0].toString(), "\n", stack[1].toString()));
      }
    }
  }

  /**
   * String ... -> String
   * 
   * @param strings
   * @return
   */
  protected static String makeMessage(String... strings) {
    StringBuffer sb = new StringBuffer();
    for (String string : strings) {
      sb.append(string);
    }
    return sb.toString();
  }

  /**
   * 에러코드 반환
   * 
   * @return
   */
  public int getErrorCode() {
    return errorCode;
  }

}
