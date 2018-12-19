package com.github.tinymini.netty.common.util;

import java.util.HashMap;
import java.util.Locale;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import com.github.tinymini.netty.common.Constants;

/**
 * 메세지 유틸
 * 
 * @author shkim
 *
 */
public class MessageUtils implements Constants {
  /** 캐시 맵 */
  private static HashMap<String, ResourceBundleMessageSource> CACHE = new HashMap<>();
  /** 설정 사용 메세지 번들 명 */
  private static String baseName;

  /**
   * 기본 메세지 번들명 설정
   * 
   * @param defaultBaseName
   */
  public static void setBaseMessageBundle(String baseName) {
    MessageUtils.baseName = baseName;
  }

  /**
   * 기본 메세지 번들명 설정
   * 
   * @param defaultBaseName
   */
  public static void setBaseMessageBundle(Class<?> clazz, String propertiesName) {
    MessageUtils.baseName = getResourceNameFromClass(clazz, propertiesName);
  }

  /**
   * classPath 기준으로 resouce명 반환
   * 
   * @param clazz
   * @param fileName
   * @return
   */
  public static String getResourceNameFromClass(Class<?> clazz, String fileName) {
    return clazz.getName().replaceFirst(clazz.getSimpleName() + "$", "") + fileName;
  }
  
  /**
   * 메세지
   * 
   * @param baseName
   * @param key
   * @param arguments
   * @return
   */
  public static String getMessage(String key, Object... arguments) {
    return getMessage(MessageUtils.baseName, key, Locale.getDefault(), null, arguments);
  }

  /**
   * 메세지
   * 
   * @param baseName
   * @param key
   * @param arguments
   * @return
   */
  public static String getMessage(String baseName, String key, Object... arguments) {
    return getMessage(baseName, key, Locale.getDefault(), null, arguments);
  }

  /**
   * 메세지
   * 
   * @param baseName
   * @param key
   * @param locale
   * @param defaultMessage
   * @param arguments
   * @return
   */
  public static String getMessage(String baseName, String key, Locale locale, String defaultMessage,
      Object... arguments) {
    if ((baseName == null) || (key == null))
      return defaultMessage;

    ResourceBundleMessageSource messageSource = CACHE.get(baseName);

    if (messageSource == null) {
      messageSource = new ResourceBundleMessageSource();
      messageSource.setBasename(baseName);

      CACHE.put(baseName, messageSource);
    }

    if (locale == null)
      locale = Locale.getDefault();

    try {
      return messageSource.getMessage(key, arguments, locale);
    } catch (NoSuchMessageException ex) {
    }

    return defaultMessage;
  }

}
