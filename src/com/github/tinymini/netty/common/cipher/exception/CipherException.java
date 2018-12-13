package com.github.tinymini.netty.common.cipher.exception;

import com.github.tinymini.netty.common.Code;
import com.github.tinymini.netty.common.exception.CustomException;

/**
 * 암/ 복호화 익셉션
 * 
 * @author shkim
 *
 */
public class CipherException extends CustomException {
  private static final long serialVersionUID = -6496100369030736121L;

  public CipherException(int errorCode, Class<?> cipherClass, String cause, String source) {
    super(errorCode, "Cipher type: ", cipherClass.getSimpleName(), ", cause: ", cause, ", source: ",
        source);
  }

  public CipherException(int errorCode, Class<?> cipherClass, String cause) {
    this(errorCode, cipherClass, cause, "");
  }

  public CipherException(Class<?> cipherClass, String cause) {
    this(Code.CIPHER_FAIL, cipherClass, cause);
  }
}
