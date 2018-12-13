package com.github.tinymini.netty.common.annotation.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.github.tinymini.netty.common.cipher.enums.DecryptType;

/**
 * 파라메터 복호화 여부 어노테이션
 * 
 * @author shkim
 * @see kr.co.tvhub.payment.resolvers.PaymentServerArgumentResolver
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Decrypt {
  /** 복호화 타입 */
  DecryptType value() default DecryptType.ALL;

  /** DecryptType.SELECT 또는 CHUNK_SELECT 일 경우 해당 파라메터 복호화 */
  String[] names() default {""};

  /** 복호화 타입 */
  String cipherType() default "";
}
