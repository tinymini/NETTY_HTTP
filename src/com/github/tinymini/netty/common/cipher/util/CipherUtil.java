package com.github.tinymini.netty.common.cipher.util;

/**
 * 암호화 유틸
 * 
 * @author shkim
 * @since 2018-04-03
 */
public interface CipherUtil {
  /**
   * 암호화
   * 
   * @param plaintext
   * @return
   */
  public String encrypt(String plaintext);

  /**
   * 복호화
   * 
   * @param ciphertext
   * @return
   */
  public String decrypt(String ciphertext);

  /** 지원 되지 않는 암호화 모드 */
  public static String UNSUPPORTED_CIPHER_MODE = "Unsupported Cipher Mode";
}
