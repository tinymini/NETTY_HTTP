package com.github.tinymini.netty.common.cipher.model;

import com.github.tinymini.netty.common.cipher.enums.DataType;
import com.github.tinymini.netty.common.enums.Encoding;

/**
 * 암/ 복호화 모델
 * 
 * @author shkim
 * @since 2018-04-03
 *
 */
public class CipherModel {
  /** 키 사이즈 */
  protected int keySize;
  /** 인코딩 */
  protected String encoding;
  /** 알고리즘 */
  protected String algorithm;
  /** 암호화 모드 */
  protected String cipherMode;
  /** 패딩 타입 */
  protected String paddingType;
  /** 결과 타입 */
  protected DataType resultType;

  /** 키 타입 */
  protected final static String KEY_TYPE = "keyType";

  public CipherModel() {
    this.encoding = Encoding.UTF_8.getName();
    this.resultType = DataType.BASE64;
  }

  public int getKeySize() {
    return keySize;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public String getCipherMode() {
    return cipherMode;
  }

  public void setCipherMode(String cipherMode) {
    this.cipherMode = cipherMode;
  }

  public String getPaddingType() {
    return paddingType;
  }

  public void setPaddingType(String paddingType) {
    this.paddingType = paddingType;
  }

  public void setKeySize(int keySize) {
    this.keySize = keySize;
  }

  public DataType getResultType() {
    return resultType;
  }

  public void setResultType(DataType resultType) {
    this.resultType = resultType;
  }

}
