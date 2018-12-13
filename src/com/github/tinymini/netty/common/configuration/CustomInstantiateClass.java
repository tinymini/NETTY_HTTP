package com.github.tinymini.netty.common.configuration;

import java.util.Map;

/**
 * 자동 생성 모델용 초기화 기능 클래스
 * 
 * @author shkim
 *
 */
public interface CustomInstantiateClass {

  /**
   * 클래스가 자동 생성 가능한지 판단
   * 
   * @param clazz
   * @return
   */
  public boolean filter(Class<?> clazz);

  /**
   * 자동생성
   * 
   * @param clazz
   * @param paramMap
   * @return
   */
  public Object initialize(Class<?> clazz, Map<String, String[]> paramMap);
}
