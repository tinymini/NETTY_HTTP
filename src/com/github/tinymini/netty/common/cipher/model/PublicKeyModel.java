package com.github.tinymini.netty.common.cipher.model;

import java.security.KeyPair;

/**
 * 공개 키 암/복호화 모델
 * 
 * @author shkim
 * @since 2018-04-03
 *
 */
public class PublicKeyModel extends CipherModel {
  /** 공개 키 */
  protected byte[] publicKey;
  /** 개인 키 */
  protected byte[] privateKey;

  protected static final String PUBLIC_KEY = "publicKey";
  protected static final String PRIVATE_KEY = "privateKey";

  public PublicKeyModel() {
    super();
    this.keySize = 2048;
  }

  public PublicKeyModel(int keySize, byte[] publicKey, byte[] privateKey) {
    super();
    this.keySize = keySize;
    this.publicKey = publicKey;
    this.privateKey = privateKey;
  }

  public byte[] getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(byte[] publicKey) {
    this.publicKey = publicKey;
  }

  public byte[] getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(byte[] privateKey) {
    this.privateKey = privateKey;
  }

  public void setKeyPair(KeyPair keyPair) {
    this.publicKey = keyPair.getPublic().getEncoded();
    this.privateKey = keyPair.getPrivate().getEncoded();
  }

}
