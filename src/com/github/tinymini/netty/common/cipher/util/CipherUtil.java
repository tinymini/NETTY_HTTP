package com.github.tinymini.netty.common.cipher.util;

import com.github.tinymini.netty.common.cipher.CipherMessages;

/**
 * 암호화 유틸
 * 
 * @author shkim
 * @since 2018-04-03
 */
public interface CipherUtil extends CipherMessages {
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
}
