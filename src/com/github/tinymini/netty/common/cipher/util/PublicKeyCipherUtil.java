package com.github.tinymini.netty.common.cipher.util;

import com.github.tinymini.netty.common.cipher.enums.DataType;
import com.github.tinymini.netty.common.cipher.model.PublicKeyModel;

/**
 * 비대칭키 암호화 관련 추상 클래스
 * 
 * @author shkim
 * @since 2018-03-20
 */
public abstract class PublicKeyCipherUtil implements CipherUtil {
  /** 알고리즘 */
  protected String algorithm;
  /** 암/복호화 모드 */
  protected String cipherMode;
  /** 인코딩 */
  protected String encoding;
  /** 공개키 */
  protected byte[] publicKey;
  /** 개인키 */
  protected byte[] privateKey;
  /** 키 사이즈 */
  protected int keySize;
  /** 리턴 타입 */
  protected DataType resultType;

  public PublicKeyCipherUtil(PublicKeyModel pkm) {
    this.encoding = pkm.getEncoding();
    this.keySize = pkm.getKeySize();
    this.cipherMode = pkm.getCipherMode();
    this.publicKey = pkm.getPublicKey();
    this.privateKey = pkm.getPrivateKey();
    this.algorithm = pkm.getAlgorithm();
    this.resultType = pkm.getResultType();
  }

  public abstract String encrypt(String plaintext);

  public abstract String decrypt(String ciphertext);
}
