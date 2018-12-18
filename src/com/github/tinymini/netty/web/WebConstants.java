package com.github.tinymini.netty.web;

import com.github.tinymini.netty.common.Constants;

/**
 * 웹 관련 상수 모음
 * 
 * @author shkim
 *
 */
public interface WebConstants extends Constants {

  /** 결과 코드 */
  public static final String HTTP_STATUS = "HTTP_STATUS";
  /** 결과 메세지 */
  public static final String MESSAGE = "message";
  /** 요청 URI */
  public static final String REQUEST_URI = "REQUEST_URI";
  /** 요청 메소드 */
  public static final String REQUEST_METHOD = "REQUEST_METHOD";
  /** 요청 내용 */
  public static final String REQUEST_BODY = "REQUEST_BODY";
  /** 요청한 호스트 주소 */
  public static final String REMOTE_HOST_ADDRESS = "REMOTE_HOST_ADDRESS";
  /** 요청한 호스트 포트 */
  public static final String REMOTE_HOST_PORT = "REMOTE_HOST_PORT";
}
