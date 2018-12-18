package com.github.tinymini.netty.core.web.service;

import com.github.tinymini.netty.core.web.handler.ApiHandler;
import com.github.tinymini.netty.core.web.handler.ApiHandlerAdapter;

/**
 * 기본 서비스
 * 
 * @author shkim
 *
 */
public class DefaultHandler extends ApiHandlerAdapter {

  @Override
  public ApiHandler execute(Object dto) {
    return setStatusIfNotExist(NOT_IMPLEMENTED);
  }
}
