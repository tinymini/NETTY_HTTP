package com.github.tinymini.netty.common.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.github.tinymini.netty.common.configuration.CustomInstantiateClass;
import com.github.tinymini.netty.common.exception.BeanException;

/**
 * 빈 컨트롤 유틸
 * 
 * @author shkim
 *
 */
public final class BeanUtils implements ApplicationContextAware {
  /** 빈 반환을 위한 컨텍스트 객체 */
  private static ApplicationContext ctx;
  /** 자동생성용 클래스 목록 */
  private static List<CustomInstantiateClass> customClassList;

  BeanUtils() {
    customClassList = new LinkedList<>();
  }

  @Override
  public void setApplicationContext(ApplicationContext arg0) throws BeansException {
    ctx = arg0;
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
    customClassList.add(customInstantiateClass);
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
    for (CustomInstantiateClass instantiateClass : customClassList) {
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
