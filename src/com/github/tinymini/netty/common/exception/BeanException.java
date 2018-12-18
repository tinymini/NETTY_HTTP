package com.github.tinymini.netty.common.exception;

import com.github.tinymini.netty.common.util.MessageUtils;
import io.netty.handler.codec.http.HttpResponseStatus;

public class BeanException extends CustomException {
  private static final long serialVersionUID = -454181360984421781L;
  private static final String NO_BEAN = "NO_BEAN";

  public <T> BeanException(HttpResponseStatus status, String beanId, Class<T> clazz) {
    super(status, MessageUtils.getMessage(MESSAGE_BUNDLE, NO_BEAN, beanId,
        clazz == null ? NULL : clazz.getName()));
  }

  public <T> BeanException(String beanId, Class<T> clazz) {
    this(INTERNAL_SERVER_ERROR, beanId, clazz);
  }

  public <T> BeanException(String beanId) {
    this(beanId, null);
  }

  public <T> BeanException(Class<T> clazz) {
    this(null, clazz);
  }
}
