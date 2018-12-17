package com.github.tinymini.netty.core.web.handler;

import java.util.List;
import java.util.Map;
import com.github.tinymini.netty.common.exception.CustomException;

/**
 * API 핸들러
 * 
 * @author shkim
 *
 */
public interface ApiHandler {

  /**
   * 유효성 검사 및 해당 오브젝트 리턴
   * 
   * @param requestData
   */
  public Object validateAndGetModel(Map<String, List<String>> requestData);

  /**
   * 실행
   * 
   * @param requestData
   */
  public ApiHandler execute(Object dto) throws CustomException;

  /**
   * 결과 반환
   * 
   * @return
   */
  public Map<String, Object> getResult();
}
