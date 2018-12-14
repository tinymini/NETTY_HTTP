package com.github.tinymini.netty.common.util;

import java.util.HashMap;
import java.util.Locale;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import com.github.tinymini.netty.common.Constants;
import com.github.tinymini.netty.common.Code;

/**
 * 메세지 유틸
 * 
 * @author shkim
 *
 */
public class MessageUtils implements Constants {
  /** 캐시 맵 */
  private static HashMap<String, ResourceBundleMessageSource> CACHE = new HashMap<>();
  /** 기본 사용 메세지 번들 명 */
  private static String DefaultBaseName = getResourceNameFromClass(Code.class, "code");
  /** 설정 사용 메세지 번들 명 */
  private static String BaseName = DefaultBaseName;

  /**
   * 기본 메세지 번들명 설정
   * 
   * @param defaultBaseName
   */
  public static void setBaseMessageBundle(String baseName) {
    MessageUtils.BaseName = baseName;
  }

  /**
   * 기본 메세지 번들명 설정
   * 
   * @param defaultBaseName
   */
  public static void setBaseMessageBundle(Class<?> clazz, String propertiesName) {
    MessageUtils.BaseName = getResourceNameFromClass(clazz, propertiesName);
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
   * 설정된 기본 메세지번들 에서 메세지 반환
   * 
   * @param errorCode
   * @return
   */
  public static String getMessage(int errorCode, Object... arguments) {
    String message = MessageUtils.getMessage(MessageUtils.BaseName, errorCode, arguments);
    if (message != null) {
      return message;
    }
    return MessageUtils.getMessage(MessageUtils.DefaultBaseName, errorCode, arguments);
  }

  /**
   * 메세지 번들에서 메세지 반환
   * 
   * @param baseName
   * @param key
   * @return
   */
  public static String getMessage(String baseName, int key, Object... arguments) {
    return getMessage(baseName, "code." + key, Locale.getDefault(), null, arguments);
  }

  /**
   * 코드 포맷
   * 
   * @param code
   * @return
   */
  public static String getStringErrorCode(int code) {
    return code < 0 ? "-" + String.format(CODE_FORMAT, -code) : String.format(CODE_FORMAT, code);
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
