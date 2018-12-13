package com.github.tinymini.netty.core.web;

import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.tinymini.netty.common.Code;
import com.github.tinymini.netty.common.util.CommonUtils;
import com.github.tinymini.netty.web.WebConstants;
import com.github.tinymini.netty.web.util.ParameterUtils;

public class ApiBase implements WebConstants, Code {
  protected Log logger = LogFactory.getLog(getClass());

  /** 리턴용 */
  public static final String INVALID_FIELD = "invalidField";

  /**
   * 파라메터 꺼내기
   * 
   * @param paramMap
   * @param key
   * @return
   */
  public static String getParam(Map<String, List<String>> paramMap, String key) {
    return ParameterUtils.getParameterFromListType(paramMap, key);
  }

  /**
   * 파라메터 넣기
   * 
   * @param paramMap
   * @param key
   * @param value
   */
  public static void putParam(Map<String, List<String>> paramMap, String key, String value) {
    ParameterUtils.putParameterList(paramMap, key, value);
  }

  /**
   * nvl 숏컷
   * 
   * @param value
   * @param defaultValue
   * @param type
   * @return
   */
  public static <T> T nvl(Object value, Object defaultValue, Class<T> type) {
    return CommonUtils.nvl(value, defaultValue, type);
  }
}
