package com.github.tinymini.netty.common.util;

import java.util.Arrays;
import java.util.Map;
import com.github.tinymini.netty.common.enums.ExceptionMessage;

/**
 * 로깅용 유틸리티
 * 
 * @author shkim
 *
 */
public class LoggingUtils {

  private LoggingUtils() {
    throw new IllegalStateException(ExceptionMessage.NOT_INSTANTIABLE.msg());
  }

  /**
   * stackTrace를 스트링으로 변환
   * 
   * @param trace
   * @param lineNumber
   * @return
   */
  public static String stackTraceToString(StackTraceElement[] trace, int lineNumber) {
    StringBuffer sb = new StringBuffer();
    String nextLine = "\n";
    sb.append(nextLine);
    int max = trace.length > lineNumber ? lineNumber : trace.length;
    for (int i = 0; i < max; i++) {
      sb.append(trace[i]).append(nextLine);
    }
    return sb.toString();
  }


  /**
   * 파라메터 맵 -> 스트링
   * 
   * @param map
   * @return
   */
  public static String paramMapToString(Map<?, ?> map) {
    StringBuffer sb = new StringBuffer();
    String START = "[";
    String END = "]";
    String COMMA = ",";
    String EQUAL = "=";
    String key = null;
    String value = null;
    Object objectValue = null;

    sb.append(START);

    if (map == null || map.isEmpty()) {
      sb.append(END);
      return sb.toString();
    }

    for (Map.Entry<?, ?> entry : map.entrySet()) {
      key = String.valueOf(entry.getKey());
      objectValue = entry.getValue();
      if (objectValue instanceof Map) {
        value = paramMapToString((Map<?, ?>) objectValue);
      } else if (objectValue.getClass().isArray()) {
        value = Arrays.toString((Object[]) objectValue);
      } else {
        value = objectValue.toString();
      }
      sb.append(key).append(EQUAL).append(value).append(COMMA);
    }

    sb.setLength(sb.length() - 1);
    sb.append(END);
    return sb.toString();
  }
}
