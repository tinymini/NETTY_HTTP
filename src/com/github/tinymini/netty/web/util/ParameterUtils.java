package com.github.tinymini.netty.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.tinymini.netty.common.util.CommonUtils;
import com.github.tinymini.netty.web.enums.ParameterType;

/**
 * 파라메터 유틸
 * 
 * @author shkim
 *
 */
public final class ParameterUtils {
  protected static final Log logger = LogFactory.getLog(ParameterUtils.class);

  /**
   * 파라메터에 해당 변수명으로 된 값 존재여부 확인
   * 
   * @param request
   * @param params 변수명들
   * @return
   */
  public static boolean hasParam(final HttpServletRequest request, final String... params) {
    if (params == null || params.length == 0) {
      return false;
    }
    String param = null;
    for (int i = 0; i < params.length; i++) {
      param = request.getParameter(params[i]);
      if (!CommonUtils.hasText(param)) {
        return false;
      }
    }
    return true;
  }


  /**
   * BODY를 가지고 있는 파라메터 타입을 스트링으로 리턴
   * 
   * @param request
   * @return
   */
  public static String getBody(final HttpServletRequest request) {
    try {
      String contentType = request.getContentType();
      if (contentType != null) {
        StringBuffer jb = new StringBuffer();
        String line = null;
        BufferedReader reader;
        reader = request.getReader();
        while ((line = reader.readLine()) != null) {
          jb.append(line);
        }
        return jb.toString();
      } else {
        return ParameterType.QUERY_STRING
            .encode(WebUtils.getParamArrayMapToListMap(request.getParameterMap()));
      }
    } catch (IOException e) {
      logger.info(e.getMessage());
      throw new RuntimeException("fail to get body");
    }
  }

  /**
   * 파라메터 맵 형식으로 키 밸류 값 입력
   * 
   * @param paramMap
   * @param key
   * @param value
   */
  public static void putParameterArray(Map<String, String[]> paramMap, String key, String value) {
    String[] values = paramMap.get(key);
    if (values != null) {
      values = Arrays.copyOf(values, values.length + 1);
      values[values.length - 1] = value;
    } else {
      paramMap.put(key, new String[] {value});
    }
  }

  /**
   * 파라메터 맵 형식으로 키 밸류 값 입력
   * 
   * @param paramMap
   * @param key
   * @param value
   */
  public static void putParameterList(Map<String, List<String>> paramMap, String key,
      Object value) {
    List<String> values = paramMap.get(key);
    if (values == null) {
      values = new ArrayList<>();
    }
    values.add(String.valueOf(value));
    paramMap.put(key, values);
  }

  /**
   * 리스트 파라메터 맵에서 값 가져오기
   * 
   * @param paramMap
   * @param key
   * @return
   */
  public static String getParameterFromListType(Map<String, List<String>> paramMap, String key) {
    if (paramMap == null || key == null) {
      throw new IllegalArgumentException("parameter is not nullable");
    }

    List<String> values = paramMap.get(key);

    for (String value : values) {
      if (value != null) {
        return value;
      }
    }
    
    throw new NullArgumentException("no value for key");
  }
  
}
