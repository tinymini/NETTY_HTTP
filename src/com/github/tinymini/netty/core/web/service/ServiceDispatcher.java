package com.github.tinymini.netty.core.web.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import com.github.tinymini.netty.common.exception.CustomException;
import com.github.tinymini.netty.common.util.BeanUtils;
import com.github.tinymini.netty.common.util.LoggingUtils;
import com.github.tinymini.netty.core.web.ApiBase;
import com.github.tinymini.netty.core.web.handler.ApiHandler;
import com.github.tinymini.netty.core.web.handler.ApiHandlerAdapter;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * 디스패쳐
 * 
 * @author shkim
 */
public class ServiceDispatcher extends ApiBase {
  private static final Log logger = LogFactory.getLog(ServiceDispatcher.class);

  /** 기본 핸들러 */
  private String defaultHandlerName;

  /** 기본 핸들러 게터 */
  public String getDefaultHandlerName() {
    return defaultHandlerName;
  }

  /** 기본 핸들러 세터 */
  public void setDefaultHandlerName(String defaultHandlerName) {
    this.defaultHandlerName = defaultHandlerName;
  }

  /**
   * 기본 핸들러를 불러옴
   * 
   * @return
   */
  public ApiHandlerAdapter getDefaultHandler() {
    return BeanUtils.getBean(defaultHandlerName, ApiHandlerAdapter.class);
  }

  /**
   * 기본 핸들러를 불러옴
   * 
   * @param e
   * @return
   */
  public ApiHandler getDefaultHandler(Exception e) {
    ApiHandlerAdapter defaultHandler = getDefaultHandler();
    if (e instanceof CustomException) {
      defaultHandler.setResultCode(((CustomException) e).getErrorCode());
    }
    logger.error(e);
    return defaultHandler;
  }

  /**
   * 기본 핸들러를 불러옴
   * 
   * @param errorCode
   * @return
   */
  public ApiHandler getDefaultHandler(int errorCode) {
    return getDefaultHandler().setResultCode(errorCode);
  }

  /**
   * 핸들러 선택 후 실행
   * 
   * @param requestMap
   * @return
   */
  public ApiHandler dispatchAndExecute(Map<String, Object> requestMap) {
    String serviceUri = String.valueOf(requestMap.get(REQUEST_URI));
    final ApplicationContext context = BeanUtils.getContext();
    if (serviceUri == null) {
      return getDefaultHandler();
    }

    Map<String, List<String>> paramMap = null;
    ApiHandlerAdapter service = null;
    String selectedPattern = null;
    // GET 파라메터 처리용
    QueryStringDecoder decoder = new QueryStringDecoder(serviceUri);
    logger.info("uri: " + serviceUri);

    // 매칭된 패턴 모음
    List<String> matches = new LinkedList<>();

    // XML 등록된 패턴과 매핑
    for (String patternString : BeanUtils.API_HANDLERS) {

      // 패턴 매칭
      if (BeanUtils.PATH_MATCHER.match(patternString, serviceUri)) {
        matches.add(patternString);
      }
    }
    // 우선순위 정렬
    Collections.sort(matches, BeanUtils.PATH_MATCHER.getPatternComparator(serviceUri));
    if (matches.size() > 0) {
      selectedPattern = matches.get(0);
      service = context.getBean(selectedPattern, ApiHandlerAdapter.class);
    }

    if (service != null && selectedPattern != null) {
      paramMap = new HashMap<>();
      // GET 파라메터 세팅
      Map<String, List<String>> getParams = decoder.parameters();
      if (logger.isDebugEnabled()) {
        logger.debug("getParams: " + LoggingUtils.paramMapToString(getParams));
      }
      paramMap.putAll(getParams);
      // REST 파라메터 매핑
      int qmIndex = serviceUri.lastIndexOf("?");
      Map<String, String> restVariables = BeanUtils.PATH_MATCHER.extractUriTemplateVariables(
          selectedPattern, qmIndex > 0 ? serviceUri.substring(0, qmIndex) : serviceUri);
      if (logger.isDebugEnabled()) {
        logger.debug("restVariables :" + LoggingUtils.paramMapToString(restVariables));
      }
      for (Map.Entry<String, String> restEntry : restVariables.entrySet()) {
        putParam(paramMap, restEntry.getKey(), restEntry.getValue());
      }
    }
    if (service == null) {
      return getDefaultHandler();
    }

    try {
      // POST 요청 처리
      String requestBody = String.valueOf(requestMap.get(REQUEST_BODY));
      service.setRequestInfo(requestMap);
      Map<String, List<String>> postParams = service.getParameterType().decode(requestBody);
      if (logger.isDebugEnabled()) {
        logger.debug("postParams: " + LoggingUtils.paramMapToString(postParams));
      }
      paramMap.putAll(postParams);

      // Service에 등록되어있는 validation 실행
      Object validatedDto = service.validateAndGetModel(paramMap);

      if (validatedDto == null) {
        return service.setResultCode(INVALID_API_PARAMETER);
      }

      // 서비스 실행
      if (logger.isDebugEnabled()) {
        logger.debug("execute:" + serviceUri);
      }
      return service.execute(validatedDto);
    } catch (Exception e) {
      logger.warn(LoggingUtils.stackTraceToString(e.getStackTrace(), 10));
      return getDefaultHandler(e);
    }
  }
}
