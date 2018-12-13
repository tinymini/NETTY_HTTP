package com.github.tinymini.netty.common.exception;

import com.github.tinymini.netty.common.Code;

public class BeanException extends CustomException {
  private static final long serialVersionUID = -454181360984421781L;
  private static final String PREFIX = "No Bean for ";
  private static final String ID_PREFIX = " Id: ";
  private static final String CLASS_PREFIX = " Class: ";


  public <T> BeanException(int errorCode, String beanId, Class<T> clazz) {
    super(errorCode, PREFIX, ID_PREFIX, beanId, CLASS_PREFIX, clazz.getName());
  }

  public <T> BeanException(String beanId, Class<T> clazz) {
    super(Code.NO_BEAN, PREFIX, ID_PREFIX, beanId, CLASS_PREFIX, clazz.getName());
  }

  public <T> BeanException(String beanId) {
    super(Code.NO_BEAN, PREFIX, ID_PREFIX, beanId);
  }

  public <T> BeanException(Class<T> clazz) {
    super(Code.NO_BEAN, PREFIX, CLASS_PREFIX, clazz.getName());
  }
}
