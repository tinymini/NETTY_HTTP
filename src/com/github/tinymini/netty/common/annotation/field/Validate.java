package com.github.tinymini.netty.common.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.github.tinymini.netty.common.enums.ValidationType;

/**
 * 필드 유효성 검사 어노테이션
 * 
 * @author shkim
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
  /** 조건 */
  String contidion() default "";

  /** 조건 타입 */
  ValidationType type() default ValidationType.NOT_NULL;

  /** 메세지 */
  String message() default "";
}
