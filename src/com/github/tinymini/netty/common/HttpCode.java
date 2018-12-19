package com.github.tinymini.netty.common;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 코드
 * 
 * @author shkim
 *
 */
public interface HttpCode {

  /** 성공 */
  public static HttpResponseStatus OK = HttpResponseStatus.OK;
  /** 미구현 */
  public static HttpResponseStatus NOT_IMPLEMENTED = HttpResponseStatus.NOT_IMPLEMENTED;
  /** 잘못된 요청 */
  public static HttpResponseStatus BAD_REQUEST = HttpResponseStatus.BAD_REQUEST;
  /** 서버 오류 */
  public static HttpResponseStatus INTERNAL_SERVER_ERROR = HttpResponseStatus.INTERNAL_SERVER_ERROR;
  /** 리소스 없음 */
  public static HttpResponseStatus NOT_FOUND = HttpResponseStatus.NOT_FOUND;
  /** 거절 */
  public static HttpResponseStatus FORBIDDEN = HttpResponseStatus.FORBIDDEN;
  /** 허용되지 않는 메소드 */
  public static HttpResponseStatus METHOD_NOT_ALLOWED = HttpResponseStatus.METHOD_NOT_ALLOWED;
  /** 인증 없음 */
  public static HttpResponseStatus UNAUTHORIZED = HttpResponseStatus.UNAUTHORIZED;
  /** 수신 불가능 */
  public static HttpResponseStatus NOT_ACCEPTABLE = HttpResponseStatus.NOT_ACCEPTABLE;
  /** 서비스 불가능 */
  public static HttpResponseStatus SERVICE_UNAVAILABLE = HttpResponseStatus.SERVICE_UNAVAILABLE;
  /** 외부 의존 실패 */
  public static HttpResponseStatus FAILED_DEPENDENCY = HttpResponseStatus.FAILED_DEPENDENCY;
}
