package com.github.tinymini.netty.common.cipher.model;

import com.github.tinymini.netty.common.cipher.util.KeyUtils;

/**
 * 대칭키 암/ 복호화 모델
 * 
 * @author shkim
 * @since 2018-02-05
 */
public class SymmetricKeyModel extends CipherModel {
  /** 블럭 사이즈 */
  protected int blockSize;
  /** 키 */
  protected byte[] key;
  /** 초기화 벡터 */
  protected byte[] iv;

  protected static final String KEY = "key";
  protected static final String IV = "iv";
  protected static final String IV_TYPE = "ivType";

  public SymmetricKeyModel() {
    super();
    this.keySize = 128;
    this.blockSize = 128;
  }

  public int getBlockSize() {
    return blockSize;
  }

  public void setBlockSize(int blockSize) {
    this.blockSize = blockSize;
  }

  public byte[] getKey() {
    return key;
  }

  public void setKey(byte[] key, int keySize) {
    this.key = KeyUtils.adjustBlockSize(key, keySize);
  }

  public byte[] getIv() {
    return iv;
  }

  public void setIv(byte[] iv, int blockSize) {
    this.iv = KeyUtils.adjustBlockSize(iv, blockSize);
  }

}
