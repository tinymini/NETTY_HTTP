package com.github.tinymini.netty.common.annotation.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 파라메터 유효성 검사 여부 어노테이션
 * 
 * @author shkim
 * @see kr.co.tvhub.payment.resolvers.PaymentServerArgumentResolver
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {

}
