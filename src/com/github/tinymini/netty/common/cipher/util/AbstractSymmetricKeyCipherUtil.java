package com.github.tinymini.netty.common.cipher.util;

import javax.crypto.SecretKey;
import com.github.tinymini.netty.common.cipher.enums.DataType;
import com.github.tinymini.netty.common.cipher.model.SymmetricKeyModel;

/**
 * 대칭키 암호화 관련 추상 클래스
 * 
 * @author shkim
 * @since 2018-03-20
 */
public abstract class AbstractSymmetricKeyCipherUtil implements CipherUtil {
  /** 암호화 알고리즘 */
  protected String algorithm;
  /** 암호화 모드 */
  protected String cipherMode;
  /** 인코딩 */
  protected String encoding;
  /** 암/복호화 키 */
  protected SecretKey key;
  /** 암/복호화 키[바이트] */
  protected byte[] byteKey;
  /** 초기화 벡터[바이트] */
  protected byte[] iv;
  /** 블럭 사이즈 */
  protected int blockSize;
  /** 키 사이즈 */
  protected int keySize;
  /** 결과 데이터 타입 */
  protected DataType resultType;

  public AbstractSymmetricKeyCipherUtil(SymmetricKeyModel skm) {
    this.encoding = skm.getEncoding();
    this.keySize = skm.getKeySize();
    this.blockSize = skm.getBlockSize();
    this.cipherMode = skm.getCipherMode();
    this.byteKey = skm.getKey();
    this.algorithm = skm.getAlgorithm();
    this.resultType = skm.getResultType();
  }

  public abstract String encrypt(String plaintext);

  public abstract String decrypt(String ciphertext);
}
