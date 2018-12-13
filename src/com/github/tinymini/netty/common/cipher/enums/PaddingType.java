package com.github.tinymini.netty.common.cipher.enums;

import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;

/**
 * 암/복호화 패딩 타입
 * 
 * @author shkim
 *
 */
public enum PaddingType {
  PKCS1Padding {
    public BlockCipherPadding getPadding() {
      throw new RuntimeException("No PKCS1Padding");
    }
  },
  PKCS5Padding {
    public BlockCipherPadding getPadding() {
      return new PKCS7Padding();
    }
  },
  PKCS7Padding {
    public BlockCipherPadding getPadding() {
      return new PKCS7Padding();
    }
  },
  ZeroBytePadding {
    public BlockCipherPadding getPadding() {
      return new ZeroBytePadding();
    }
  };
  /**
   * 해당되는 패딩 객체를 얻는다
   * 
   * @return
   */
  public abstract BlockCipherPadding getPadding();
}
