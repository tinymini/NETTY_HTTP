package com.github.tinymini.netty.common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.tinymini.netty.web.util.ParameterUtils;

/**
 * 조건 별 추가 파라메터 존재시
 * 
 * @author shkim
 *
 */
public abstract class AdditionalParameter {

  /** 추가 파라메터 */
  Map<String, List<String>> paramMap = new HashMap<>();

  /**
   * 파라메터 추가
   * 
   * @param key
   * @param value
   */
  public void putParameter(String key, Object value){
    ParameterUtils.putParameterList(this.paramMap, key, value);
  };

  /**
   * 파라메터 획득
   * 
   * @param key
   * @return
   */
  public String getParameter(String key){
    return ParameterUtils.getParameterFromListType(this.paramMap, key);
  };

  /**
   * 파라메터 맵 획득
   * 
   * @return
   */
  public Map<String, List<String>> getListTypeParameters(){
    return this.paramMap;
  };
}
