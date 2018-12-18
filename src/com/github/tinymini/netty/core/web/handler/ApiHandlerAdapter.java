package com.github.tinymini.netty.core.web.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.tinymini.netty.common.exception.CustomException;
import com.github.tinymini.netty.common.util.ClassUtils;
import com.github.tinymini.netty.core.web.ApiBase;
import com.github.tinymini.netty.web.enums.ParameterType;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * API 공통
 * 
 * @author shkim
 *
 */
public abstract class ApiHandlerAdapter extends ApiBase implements ApiHandler {
  
  private static final String SHOULD_BE_OVERRIDE = "should be override";
  /** 파라메터 타입 */
  protected ParameterType parameterType = ParameterType.QUERY_STRING;
  /** 요청 정보 */
  protected Map<String, Object> requestInfo;
  /** 응답 객체 */
  protected Map<String, Object> resultObject = new HashMap<>();
  /** 에러 데이터 */
  protected Map<String, Object> errorMap = new HashMap<>();

  public Map<String, Object> getResult() {
    return this.resultObject;
  }

  public ParameterType getParameterType() {
    return parameterType;
  }

  public ApiHandler setParameterType(ParameterType parameterType) {
    this.parameterType = parameterType;
    return this;
  }

  public ApiHandler setRequestInfo(Map<String, Object> requestInfo) {
    this.requestInfo = requestInfo;
    return this;
  }

  public ApiHandler setStatus(HttpResponseStatus status) {
    this.resultObject.put(HTTP_STATUS, status);
    return this;
  }

  public ApiHandler setStatusIfNotExist(HttpResponseStatus status) {
    if (this.resultObject.get(HTTP_STATUS) == null) {
      this.resultObject.put(HTTP_STATUS, status);
    }
    return this;
  }

  public ApiHandler putResult(String key, Object value) {
    this.resultObject.put(key, value);
    return this;
  }

  public Map<String, Object> getErrorMap() {
    return errorMap;
  }

  @Override
  public Object validateAndGetModel(Map<String, List<String>> requestData) {
    throw new RuntimeException(SHOULD_BE_OVERRIDE);
  }

  @Override
  public ApiHandler execute(Object dto) throws CustomException {
    throw new RuntimeException(SHOULD_BE_OVERRIDE);
  }

  /**
   * 유효성 검사 후 결과 리턴
   * 
   * @param t
   * @param requestData
   * @return
   */
  public <T> T validateAndSetStatus(T model, Map<String, List<String>> requestData) {
    ClassUtils.autoComplete(model, requestData, this.errorMap);
    if (this.errorMap.size() > 0) {
      putResult(INVALID_FIELD, this.errorMap);
      setStatus(BAD_REQUEST);
      return null;
    } else {
      return model;
    }
  }

}
