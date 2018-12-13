package com.github.tinymini.netty.common.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import com.github.tinymini.netty.common.enums.ExceptionMessage;
import com.github.tinymini.netty.common.exception.ConvertException;

/**
 * 기본 기능
 * 
 * @author shkim
 * @since 2018-04-04
 */
public final class CommonUtils {

  private CommonUtils() {
    throw new IllegalStateException(ExceptionMessage.NOT_INSTANTIABLE.msg());
  }

  /** 기본 -> 객체 맵 */
  private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS;

  static {
    PRIMITIVES_TO_WRAPPERS = new HashMap<Class<?>, Class<?>>();
    PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
    PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
    PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
    PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
    PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
    PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
    PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
    PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
    PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);
  }

  /**
   * 널 밸류
   * 
   * 밸류가 널일 경우 공백 리턴
   * 
   * @param value 입력값
   * @return
   */
  public static String nvl(Object value) {
    return nvl(value, "", String.class);
  }

  /**
   * 널 밸류
   * 
   * 디폴트 값의 형으로 리턴 - 디폴트가 널일 경우 밸류의 형으로 리턴
   * 
   * @param value 입력값
   * @param defaultValue 기본값
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T nvl(Object value, T defaultValue) {
    return nvl(value, defaultValue,
        (Class<T>) (defaultValue == null ? value : defaultValue).getClass());
  }

  /**
   * 널 밸류 리턴 클래스의 형으로 리턴
   * 
   * @param value 입력값
   * @param defaultValue 기본값
   * @param returnClass 리턴 형태
   * @return
   */
  public static <T> T nvl(Object value, Object defaultValue, Class<T> returnClass) {
    T result = convert(isNullOrEmpty(value) ? defaultValue : value, returnClass);
    return result == null ? convert(defaultValue, returnClass) : result;
  }

  /**
   * 타입 변환 실패시 null 리턴
   * 
   * @param source
   * @param convertType
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T convert(Object source, Class<T> convertType) {

    if (source == null || convertType == null) {
      throw new ConvertException(source.getClass(), convertType);
    }

    if (convertType.isEnum()) {
      return convertEnum(String.valueOf(source), convertType);
    } else if (ClassUtils.isPrimitiveOrWrapper(convertType)) {
      return convertPrimitive(source, convertType);
    } else {
      return (T) source;
    }
  }

  /**
   * 원시값 변환
   * 
   * 오브젝트 -> 원시
   * 
   * @param source
   * @param primitiveType
   * @return
   */
  @SuppressWarnings("unchecked")
  private static <T> T convertPrimitive(Object source, Class<T> detinationType) {
    try {
      // 스트링 -> 해당 원시값의 생성자로 변환
      return (T) ClassUtils.resolvePrimitiveIfNecessary(detinationType).getConstructor(String.class)
          .newInstance(String.valueOf(source));
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new ConvertException(source.getClass(), detinationType);
    }
  }

  /**
   * 이넘값 변환
   * 
   * 오브젝트 -> 이넘
   * 
   * @param source
   * @param primitiveType
   * @return
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <T> T convertEnum(Object source, Class<T> detinationType) {
    try {
      // enum의 valueof 사용
      if (source instanceof String) {
        return (T) Enum.valueOf((Class<Enum>) detinationType, (String) source);
      } else {
        return (T) source;
      }
    } catch (IllegalArgumentException e) {
      throw new ConvertException(source.getClass(), detinationType);
    }
  }



  /**
   * 널 체크 콜렉션, 맵, 배열, 스트링 체크 for http
   * 
   * @param object
   * @return
   */
  public static boolean isNullOrEmpty(Object object) {
    if (object == null) {
      return true;
    }
    if (ClassUtils.isPrimitiveOrWrapper(object.getClass())) {
      return false;
    }
    if (object instanceof Collection) {
      return ((Collection<?>) object).isEmpty();
    }
    if (object instanceof Map) {
      return ((Map<?, ?>) object).isEmpty();
    }
    if (object instanceof Object[]) {
      return Array.getLength(object) == 0;
    }
    String string = String.valueOf(object);
    if (hasText(string)) {
      return ("null".equalsIgnoreCase(string) || "undefined".equalsIgnoreCase(string));
    } else {
      return true;
    }
  }

  /**
   * 다중 스트링 체커
   * 
   * @param strings
   * @return
   */
  public static boolean hasText(String... strings) {
    if (strings == null) {
      return false;
    }
    int len = strings.length;
    for (int i = 0; i < len; i++) {
      if (!StringUtils.hasText(strings[i])) {
        return false;
      }
    }
    return true;
  }


  /**
   * 맵 세이프
   * 
   * @param map
   * @param key
   * @param defaultValue
   * @return
   */
  public static String get(Map<String, ?> map, String key, String defaultValue) {
    return nvl(map.get(key), defaultValue);
  }

  /**
   * 배열 합치기
   * 
   * @param arrays
   * @return
   */
  public static <T> T[] concat(T[] first, T[] second) {
    T[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  /**
   * 바이트 -> 인트
   * 
   * @param b
   * @return
   */
  public static int byteArrayToInt(byte[] b) {
    return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
  }

  /**
   * 인트 -> 바이트
   * 
   * @param a
   * @return
   */
  public static byte[] intToByteArray(int a) {
    return new byte[] {(byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF),
        (byte) ((a >> 8) & 0xFF), (byte) (a & 0xFF)};
  }

  /**
   * 기본 타입 -> 객체 타입
   * 
   * @param c 기본 타입
   * @return 객체 타입
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> wrap(Class<T> c) {
    return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
  }
}
