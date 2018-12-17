package com.github.tinymini.netty.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import com.github.tinymini.netty.common.configuration.CustomInstantiateClass;
import com.github.tinymini.netty.common.exception.BeanException;

/**
 * 빈 컨트롤 유틸
 * 
 * @author shkim
 *
 */
public final class BeanUtils implements ApplicationContextAware, BeanFactoryPostProcessor {
  private static final Log logger = LogFactory.getLog(BeanUtils.class);
  /** 빈 반환을 위한 컨텍스트 객체 */
  private static ApplicationContext ctx;
  /** 빈 등록을 위한 팩토리 */
  private static ConfigurableListableBeanFactory factory;
  /** 초기화 여부 */
  private static boolean isInitialized = false;
  /** 자동생성용 클래스 목록 */
  private static final List<CustomInstantiateClass> CUSTOM_CLASS_LIST = new LinkedList<>();
  /** 핸들러 등록용 맵 */
  public static final List<String> API_HANDLERS = new CopyOnWriteArrayList<>();
  /** URI pathmatcher 전역 */
  public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory factory)
      throws BeansException {
    BeanUtils.factory = factory;
    if (ctx != null && isInitialized == false) {
      isInitialized = true;
      initialize();
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext ctx) throws BeansException {
    BeanUtils.ctx = ctx;
    if (factory != null && isInitialized == false) {
      isInitialized = true;
      initialize();
    }
  }

  private synchronized void initialize() {
    logger.info(this.getClass().getSimpleName() + " is initialized");
    final ApplicationContext context = ctx;
    final String[] beanNames = context.getBeanNamesForType(Object.class);
    final BeanDefinitionRegistry registry = ((BeanDefinitionRegistry) factory);

    // bean 이름으로 핸들러 등록
    for (final String beanName : beanNames) {
      if (beanName.startsWith("/")) {
        final Object bean = context.getBean(beanName);
        logger.info("Controller: " + beanName);
        final List<String> aliases =
            new CopyOnWriteArrayList<>(Arrays.asList(context.getAliases(beanName)));
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
                logger.info(path + " is mapped by " + arg0.getName());
                synchronized (registry) {
                  registry.registerBeanDefinition(path, beanDefinition);
                }
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

  public static ApplicationContext getContext() {
    return ctx;
  }

  public static void setContext(ApplicationContext ctx) {
    BeanUtils.ctx = ctx;
  }

  /**
   * 아이디와 클래스로 빈 반환
   * 
   * @param beanId
   * @param clazz
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T getBean(String beanId, Class<T> clazz) {
    try {
      return (T) ctx.getBean(beanId);
    } catch (NoSuchBeanDefinitionException e) {
      throw new BeanException(beanId, clazz);
    }
  }

  /**
   * 타입으로 빈 반환
   * 
   * @param beanId
   * @return
   */
  public static <T> T getBean(Class<T> clazz) {
    try {
      return ctx.getBean(clazz);
    } catch (NoSuchBeanDefinitionException e) {
      throw new BeanException(clazz);
    }
  }

  /**
   * 아이디로 빈 반환
   * 
   * @param beanId
   * @return
   */
  public static Object getBean(String beanId) {
    try {
      return ctx.getBean(beanId);
    } catch (NoSuchBeanDefinitionException e) {
      throw new BeanException(beanId);
    }
  }

  /**
   * 커스텀 초기화 클래스 추가
   * 
   * @param customInstantiateClass
   */
  public static void addInstantiateCustomClass(CustomInstantiateClass customInstantiateClass) {
    CUSTOM_CLASS_LIST.add(customInstantiateClass);
  }

  /**
   * 커스텀 클래스 초기화
   * 
   * @param clazz
   * @param paramMap
   * @return
   */
  public static Object instantiateCustomClass(Class<?> clazz, Map<String, String[]> paramMap) {
    Object customClass = null;
    for (CustomInstantiateClass instantiateClass : CUSTOM_CLASS_LIST) {
      if (instantiateClass.filter(clazz)) {
        customClass = instantiateClass.initialize(clazz, paramMap);
        if (customClass != null) {
          return customClass;
        }
      }
    }
    return org.springframework.beans.BeanUtils.instantiate(clazz);
  }
}
