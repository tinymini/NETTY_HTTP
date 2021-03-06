package com.github.tinymini.netty.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ReflectionUtils;
import com.github.tinymini.netty.common.HttpCode;
import com.github.tinymini.netty.common.annotation.field.Validate;
import com.github.tinymini.netty.common.enums.ExceptionMessage;
import com.github.tinymini.netty.common.enums.WritingCase;
import com.github.tinymini.netty.common.model.AdditionalParameter;
import com.github.tinymini.netty.web.util.WebUtils;

/**
 * 모델 관련 기능 유틸
 * 
 * @author shkim
 *
 */
public final class ClassUtils implements HttpCode {
  private static final String MESSAGE_BUNDLE =
      MessageUtils.getResourceNameFromClass(ClassUtils.class, "messages");
  private static final String INVALID_FIELD_DATA = "INVALID_FIELD_DATA";
  private static final String SET_METHOD_PREFIX = "set";
  private static final String VALIDATE_METHOD_PREFIX = "validate";

  private ClassUtils() {
    throw new IllegalStateException(ExceptionMessage.NOT_INSTANTIABLE.msg());
  }

  private static final Log logger = LogFactory.getLog(ClassUtils.class);

  /**
   * 멤버 변수를 찾아서 설정된 클래스에 포함되면 값을 자동으로 세팅한다. set메서드 필수
   * 
   * @param instance 자동 세팅 객체
   * @param map 자동 세팅 값이 들어있는 맵
   * @param error 에러 정보 맵
   */
  public static <C> C autoComplete(final C instance, final Map<String, ?> map) {
    return autoComplete(instance, map, null);
  }

  /**
   * 멤버 변수를 찾아서 설정된 클래스에 포함되면 값을 자동으로 세팅한다. set메서드 필수
   * 
   * @param instance 자동 세팅 객체
   * @param sourceMap 자동 세팅 값이 들어있는 맵
   * @param error 에러 정보 맵
   */
  public static <C> C autoComplete(final C instance, final Map<String, ?> sourceMap,
      final Map<String, Object> errorMap) {
    final Map<String, String> map = WebUtils.getSimpleMap(sourceMap);
    final Class<?> instanceClass = instance.getClass();
    final boolean validFlag = errorMap != null;
    final List<String> fieldList = new LinkedList<>();

    if (logger.isInfoEnabled()) {
      logger.info("parameters: " + LoggingUtils.paramMapToString(map));
    }
    ReflectionUtils.doWithFields(instanceClass, new ReflectionUtils.FieldCallback() {
      @Override
      public void doWith(Field paramField) throws IllegalArgumentException, IllegalAccessException {
        // 변수 세팅 여부
        boolean isSettable = false;
        boolean isError = false;
        Validate annotation = paramField.getAnnotation(Validate.class);
        // 필드명 얻기
        String fieldName = paramField.getName();
        try {
          // 필드명으로 값 가져와서 필드 타입으로 변환
          Object source = map.get(fieldName);
          Object value = CommonUtils.convert(source, paramField.getType());

          boolean isNullOrEmpty = CommonUtils.isNullOrEmpty(value);
          if (validFlag && annotation != null) {
            // errorMap 존재 하고 validate annotaion 존재시
            if (!isNullOrEmpty) {
              switch (annotation.type()) {
                // validation 메소드명 찾기
                case FUNCTION: // 벨리데이션 메소드명으로 벨리데이션 실행

                  // 정해진 이름 또는 , validateCapitalized 또는 validateCamelCase 로 validationMethod 찾기
                  String[] validationMethodNames =
                      {annotation.contidion(), VALIDATE_METHOD_PREFIX + capitalize(fieldName),
                          WritingCase.camelCase.change(VALIDATE_METHOD_PREFIX, fieldName)};

                  if (logger.isDebugEnabled()) {
                    logger.debug(
                        "find validateMethod names :" + Arrays.toString(validationMethodNames));
                  }

                  Method validationMethod = null;
                  for (int i = 0; i < validationMethodNames.length; i++) {
                    String validationMethodName = validationMethodNames[i];
                    if (validationMethodName == null) {
                      continue;
                    }
                    validationMethod = ReflectionUtils.findMethod(instanceClass,
                        validationMethodName, paramField.getType());
                    if (validationMethod != null) {
                      break;
                    }
                  }

                  try {
                    if ((boolean) validationMethod.invoke(instance,
                        CommonUtils.nvl(value, null, paramField.getType()))) {
                      isSettable = true;
                    } else {
                      isError = true;
                    }
                  } catch (InvocationTargetException e) {
                    isError = true;
                  }
                  break;
                case REGEX: // 정규식
                  Pattern pattern = Pattern.compile(annotation.contidion());
                  Matcher matcher = pattern.matcher(String.valueOf(source));
                  if (matcher.matches()) {
                    isSettable = true;
                  } else {
                    isError = true;
                  }
                  break;
                default:
                  isSettable = true;
                  break;
              }
            } else {
              isError = true;
            }
          } else if (!isNullOrEmpty) {
            isSettable = true;
          }
          // 값 설정 가능시
          if (isSettable) {
            // 필드명 setCapitalized 또는 setCamelCase 로 setMethod 찾기
            String[] setMethodNames = {SET_METHOD_PREFIX + capitalize(fieldName),
                WritingCase.camelCase.change(SET_METHOD_PREFIX, fieldName)};

            if (logger.isDebugEnabled()) {
              logger.debug("find setMethod names :" + Arrays.toString(setMethodNames));
            }

            Method setMethod = null;
            for (int i = 0; i < setMethodNames.length; i++) {
              setMethod = ReflectionUtils.findMethod(instanceClass, setMethodNames[i],
                  paramField.getType());
              if (setMethod != null) {
                break;
              }
            }

            if (setMethod != null) { // setMethod 존재시
              setMethod.invoke(instance,
                  CommonUtils.nvl(value, paramField.get(instance), paramField.getType()));
            } else if (Modifier.PUBLIC == paramField.getModifiers()) {
              paramField.set(instance,
                  CommonUtils.nvl(value, paramField.get(instance), paramField.getType()));
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          isError = true;
        }
        // 에러일 경우 메세지 추가
        if (isError && validFlag) {
          String errorMessage = null;
          if (annotation != null) {
            errorMessage = annotation.message();
          }
          if (!CommonUtils.hasText(errorMessage)) {
            errorMessage = MessageUtils.getMessage(MESSAGE_BUNDLE, INVALID_FIELD_DATA, fieldName);
          }
          errorMap.put(fieldName, errorMessage);
        }
        paramField.setAccessible(false);
      }
    }, new ReflectionUtils.FieldFilter() {
      @Override
      public boolean matches(Field paramField) {
        // 맵에 키가 존재시
        if ((map.containsKey(paramField.getName()))
            // 벨리데이션 조건이 true 이고 밸리데이션 어노테이션이 있을경우에
            || (validFlag == true && paramField.getAnnotation(Validate.class) != null)) {
          paramField.setAccessible(true);
          fieldList.add(paramField.getName());
          return true;
        }
        return false;
      }
    });
    // 추가 파라메터 존재시 맵에 세팅
    if (instance instanceof AdditionalParameter) {
      for (Map.Entry<String, String> entry : map.entrySet()) {
        String key = entry.getKey();
        if (!fieldList.contains(key)) {
          ((AdditionalParameter) instance).putParameter(key, entry.getValue());
        }
      }
    }
    if (logger.isInfoEnabled() && errorMap != null && errorMap.size() > 0) {
      logger.info("error: " + LoggingUtils.paramMapToString(errorMap));
    }
    return instance;
  }

  /**
   * 클래스에 존재하는 필드 전체 반환(부모 이하)
   * 
   * @param current 현재 검사 클래스
   * @param parent 최상위 클래스
   * @return
   */
  public static <T> Field[] getFields(Class<?> current, Class<T> parent) {
    // 클래스가 최상위가 아닐경우 재귀적으로 부모 필드 배열과 병합
    if (!current.equals(parent)) {
      return CommonUtils.concat(current.getDeclaredFields(),
          getFields(current.getSuperclass(), parent));
      // 최상위 일경우 자신 필드 배열 반환
    } else {
      return current.getDeclaredFields();
    }
  }

  /**
   * 클래스에 존재하는 메서드 전체 반환(부모 이하)
   * 
   * @param current 현재 검사 클래스
   * @param parent 최상위 클래스
   * @return
   */
  public static <T> Method[] getMethods(Class<?> current, Class<T> parent) {
    // 클래스가 최상위가 아닐경우 재귀적으로 부모 필드 배열과 병합
    if (!current.equals(parent)) {
      return CommonUtils.concat(current.getDeclaredMethods(),
          getMethods(current.getSuperclass(), parent));
      // 최상위 일경우 자신 필드 배열 반환
    } else {
      return current.getDeclaredMethods();
    }
  }

  /**
   * 첫글자만 대문자로 변경
   * 
   * @param source
   * @return
   */
  private static String capitalize(String source) {
    return source.substring(0, 1).toUpperCase() + source.substring(1);
  }

}
