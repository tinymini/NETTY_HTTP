package com.github.tinymini.netty.common.annotation.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * path 로 요청 설정 어노테이션
 * 
 * @author shkim
 * @see kr.co.tvhub.payment.resolvers.PaymentServerArgumentResolver
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathSetting {

}
