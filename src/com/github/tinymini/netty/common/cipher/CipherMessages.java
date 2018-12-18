package com.github.tinymini.netty.common.cipher;

/**
 * 코드
 * 
 * @author shkim
 *
 */
public interface CipherMessages {
  /** 메세지 번들 */
  public static String MESSAGE_BUNDLE = "messages";
  /** 잘못된 암호화 모델 */
  public static String INVALID_CIPHER_MODEL = "INVALID_CIPHER_MODEL";
  /** 암호화 실패 */
  public static String ENCRYPTION_FAIL = "ENCRYPTION_FAIL";
  /** 복호화 실패 */
  public static String DECRYPTION_FAIL = "DECRYPTION_FAIL";
}
