package com.github.tinymini.netty.common.cipher.exception;

import com.github.tinymini.netty.common.cipher.CipherMessages;
import com.github.tinymini.netty.common.exception.CustomException;
import com.github.tinymini.netty.common.util.MessageUtils;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 암/ 복호화 익셉션
 * 
 * @author shkim
 *
 */
public class CipherException extends CustomException implements CipherMessages {
  private static final long serialVersionUID = -6496100369030736121L;
  private static String MESSAGE_BUNDLE = CipherMessages.MESSAGE_BUNDLE;

  public CipherException(HttpResponseStatus status, Class<?> cipherClass, String cause, String source,
      boolean isEncryption) {
    super(status,
        MessageUtils.getMessage(MESSAGE_BUNDLE, isEncryption ? ENCRYPTION_FAIL : DECRYPTION_FAIL,
            cipherClass.getSimpleName(), cause, source));
  }

  public CipherException(Class<?> cipherClass, String cause, String source, boolean isEncryption) {
    this(INTERNAL_SERVER_ERROR, cipherClass, cause, source, isEncryption);
  }

  public CipherException(Class<?> cipherClass, String cause, boolean isEncryption) {
    this(cipherClass, cause, NULL, isEncryption);
  }

  public CipherException(Class<?> cipherClass, String cause) {
    super(INTERNAL_SERVER_ERROR,
        MessageUtils.getMessage(MESSAGE_BUNDLE, INVALID_CIPHER_MODEL, cipherClass, cause));
  }
}
