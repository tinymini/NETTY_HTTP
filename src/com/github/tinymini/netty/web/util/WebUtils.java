package com.github.tinymini.netty.web.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.NullArgumentException;
import com.github.tinymini.netty.common.enums.ExceptionMessage;
import com.github.tinymini.netty.common.util.CommonUtils;

/**
 * 웹 관련 유틸
 * 
 * @author shkim
 *
 */
public class WebUtils {

  private WebUtils() {
    throw new IllegalStateException(ExceptionMessage.NOT_INSTANTIABLE.msg());
  }

  /**
   * 리퀘스트 세이프
   * 
   * @param request
   * @param key
   * @param defaultValue
   * @return
   */
  public static String get(HttpServletRequest request, String key, String defaultValue) {
    return CommonUtils.nvl(request.getAttribute(key), defaultValue);
  }

  /**
   * 리퀘스트 세이프
   * 
   * @param request
   * @param key
   * @return
   */
  public static String get(HttpServletRequest request, String key) {
    return get(request, key, "");
  }

  /**
   * uri의 마지막 부분을 구한다
   * 
   * @param request
   * @return
   */
  public static String getUriLast(HttpServletRequest request) {
    String requestUri = request.getRequestURI();
    String contextPath = request.getContextPath();
    return requestUri.substring(requestUri.lastIndexOf(contextPath) + contextPath.length() + 1,
        requestUri.lastIndexOf("."));
  }

  /**
   * 파라메터 맵을 단순 맵 형태로 변형 / 0번재 인덱스 요소로 세팅
   * 
   * @param paramMap
   * @return
   */
  @SuppressWarnings("unchecked")
  public static Map<String, String> getSimpleMap(Map<String, ?> paramMap) {

    if (paramMap == null) {
      throw new NullArgumentException("Need parameterMap - Map<String, Object>");
    }

    Map<String, String> simpleMap = new HashMap<>();
    ParamMapType type = null;

    for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
      Object value = entry.getValue();
      // 첫번째 요소로 맵 타입 확인
      if (type == null) {
        if (value instanceof List) {
          type = ParamMapType.LIST;
        } else if (value.getClass().isArray()) {
          type = ParamMapType.ARRAY;
        } else if (value instanceof String) {
          return (Map<String, String>) paramMap;
        }
      }

      switch (type) {
        case LIST:
          value = ((List<String>) value).get(0);
          break;
        case ARRAY:
          value = ((String[]) value)[0];
          break;
      }

      simpleMap.put(entry.getKey(), String.valueOf(value));
    }
    return simpleMap;
  }

  /**
   * 파라메터 맵을 단순 맵 형태로 변형 / 0번재 인덱스 요소로 세팅
   * 
   * @param paramMap
   * @return
   */
  public static Map<String, List<String>> getParamArrayMapToListMap(
      Map<String, String[]> paramMap) {
    if (paramMap == null) {
      throw new NullArgumentException("Need parameterMap - Map<String, String[]>");
    }

    Map<String, List<String>> listMap = new HashMap<>();

    for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
      String[] value = entry.getValue();
      listMap.put(entry.getKey(), Arrays.asList(value));
    }
    return listMap;
  }

  /**
   * 파라메터 맵 변환용 이넘
   * 
   * @author shkim
   *
   */
  enum ParamMapType {
    ARRAY, LIST
  }



}
