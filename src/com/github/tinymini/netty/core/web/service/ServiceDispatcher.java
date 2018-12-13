package com.github.tinymini.netty.core.web.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import com.github.tinymini.netty.common.exception.CustomException;
import com.github.tinymini.netty.common.util.BeanUtils;
import com.github.tinymini.netty.core.web.ApiBase;
import com.github.tinymini.netty.core.web.handler.ApiHandler;
import com.github.tinymini.netty.core.web.handler.ApiHandlerAdapter;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * 디스패쳐
 * 
 * @author shkim
 */
public class ServiceDispatcher extends ApiBase implements InitializingBean {
  private static final Log logger = LogFactory.getLog(ServiceDispatcher.class);

  /** 기본 핸들러 */
  private String defaultHandlerName;
  /** 핸들러 등록용 맵 */
  private static final List<String> API_HANDLERS = new ArrayList<>();
  /** URI pathmatcher 전역 */
  private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

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

  @Override
  /** 초기화 코드 */
  public void afterPropertiesSet() throws Exception {
    final ApplicationContext context = BeanUtils.getContext();
    String[] beanNames = context.getBeanNamesForType(Object.class);
    final DefaultListableBeanFactory factory =
        (DefaultListableBeanFactory) ((ConfigurableApplicationContext) context).getBeanFactory();
    // bean 이름으로 핸들러 등록
    for (final String beanName : beanNames) {
      if (beanName.startsWith("/")) {
        final Object bean = context.getBean(beanName);
        final List<String> aliases = new LinkedList<>(Arrays.asList(context.getAliases(beanName)));
        aliases.add(beanName);

        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {

          @Override
          public void doWith(Method arg0) throws IllegalArgumentException, IllegalAccessException {
            RequestMapping mapping = arg0.getAnnotation(RequestMapping.class);
            String[] values = mapping.value();
            Object handler;
            try {
              handler = arg0.invoke(bean);
            } catch (InvocationTargetException e) {
              e.printStackTrace();
              throw new RuntimeException(e);
            }

            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            ConstructorArgumentValues constValues = new ConstructorArgumentValues();
            constValues.addGenericArgumentValue(bean);

            beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            beanDefinition.setBeanClass(handler.getClass());
            beanDefinition.setConstructorArgumentValues(constValues);

            for (String alias : aliases) {
              for (String value : values) {
                String path = PATH_MATCHER.combine(alias, value);
                factory.registerBeanDefinition(path, beanDefinition);
                API_HANDLERS.add(path);
              }
            }
          }
        }, new ReflectionUtils.MethodFilter() {

          @Override
          public boolean matches(Method arg0) {
            return arg0.getAnnotation(RequestMapping.class) != null;
          }
        });
      }
    }
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
    for (String patternString : API_HANDLERS) {

      // 패턴 매칭
      if (PATH_MATCHER.match(patternString, serviceUri)) {
        matches.add(patternString);
      } ;
    }
    // 우선순위 정렬
    Collections.sort(matches, PATH_MATCHER.getPatternComparator(serviceUri));
    if (matches.size() > 0) {
      selectedPattern = matches.get(0);
      service = context.getBean(selectedPattern, ApiHandlerAdapter.class);
    }

    if (service != null && selectedPattern != null) {
      paramMap = new HashMap<>();
      // GET 파라메터 세팅
      paramMap.putAll(decoder.parameters());
      // REST 파라메터 매핑
      Map<String, String> restVariables =
          PATH_MATCHER.extractUriTemplateVariables(selectedPattern, serviceUri);
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
      paramMap.putAll(service.getParameterType().decode(requestBody));

      // Service에 등록되어있는 validation 실행
      Object validatedDto = service.validateAndGetModel(paramMap);

      if (validatedDto == null) {
        return service.setResultCode(INVALID_API_PARAMETER);
      }

      // 서비스 실행
      return service.execute(validatedDto);
    } catch (Exception e) {
      return getDefaultHandler(e);
    }
  }
}
