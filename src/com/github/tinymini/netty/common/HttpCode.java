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

}
