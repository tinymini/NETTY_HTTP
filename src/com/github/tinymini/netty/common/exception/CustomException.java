package com.github.tinymini.netty.common.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.tinymini.netty.common.HttpCode;
import com.github.tinymini.netty.common.util.LoggingUtils;
import com.github.tinymini.netty.common.util.MessageUtils;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 메세지 유틸 포함 익셉션
 * 
 * @author shkim
 *
 */
public class CustomException extends RuntimeException implements HttpCode {
  private static final long serialVersionUID = 7420063159681514892L;
  protected final Log logger = LogFactory.getLog(getClass());
  protected static final String MESSAGE_BUNDLE =
      MessageUtils.getResourceNameFromClass(CustomException.class, "messages");
  protected static final String NULL = "NULL";
  protected HttpResponseStatus status;

  protected CustomException(String... messages) {
    this(INTERNAL_SERVER_ERROR, messages);
  }

  protected CustomException(HttpResponseStatus status, String... messages) {
    super(makeMessage(messages));
    this.status = status;
  }

  protected CustomException(HttpResponseStatus status, Throwable cause) {
    this(status, cause.getMessage());

    if (logger.isInfoEnabled()) {
      StackTraceElement[] stack = cause.getStackTrace();
      if (stack.length > 2) {
        logger.warn(LoggingUtils.stackTraceToString(stack, 20));
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
  public HttpResponseStatus getStatus() {
    return this.status;
  }

}
