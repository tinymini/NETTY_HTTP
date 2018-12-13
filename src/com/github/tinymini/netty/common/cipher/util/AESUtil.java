package com.github.tinymini.netty.common.cipher.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import com.github.tinymini.netty.common.Code;
import com.github.tinymini.netty.common.cipher.enums.CipherMode;
import com.github.tinymini.netty.common.cipher.exception.CipherException;
import com.github.tinymini.netty.common.cipher.model.SymmetricKeyModel;

/**
 * AES 암호화 관련 - CBC/ ECB 지원
 * 
 * @author shkim
 * @since 2018-02-05
 */
public final class AESUtil extends AbstractSymmetricKeyCipherUtil {
  /** 암호화 객체 */
  private Cipher cipher;

  public AESUtil(SymmetricKeyModel skm) {
    super(skm);
    try {
      this.cipher =
          CipherInstanceHelper.generate(this.algorithm, this.cipherMode, skm.getPaddingType());
      this.key = new SecretKeySpec(this.byteKey, this.algorithm);
      this.iv = skm.getIv();
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new CipherException(this.getClass(), e.getMessage());
    }
  }

  public String encrypt(String plaintext) {
    try {
      if (this.cipherMode.equals(CipherMode.CBC.name())) {
        return this.resultType
            .convert(doFinal(Cipher.ENCRYPT_MODE, this.iv, plaintext.getBytes(this.encoding)));
      } else if (this.cipherMode.equals(CipherMode.ECB.name())) {
        return this.resultType
            .convert(doFinal(Cipher.ENCRYPT_MODE, plaintext.getBytes(this.encoding)));
      } else {
        throw new CipherException(Code.ENCRYPTION_FAIL, this.getClass(),
            UNSUPPORTED_CIPHER_MODE, plaintext);
      }
    } catch (UnsupportedEncodingException | NullPointerException | InvalidKeyException
        | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
      throw new CipherException(Code.ENCRYPTION_FAIL, this.getClass(), e.getMessage(),
          plaintext);
    }
  }

  public String decrypt(String ciphertext) {
    try {
      if (this.cipherMode.equals(CipherMode.CBC.name())) {
        return new String(
            doFinal(Cipher.DECRYPT_MODE, this.iv, this.resultType.convert(ciphertext)),
            this.encoding);
      } else if (this.cipherMode.equals(CipherMode.ECB.name())) {
        return new String(doFinal(Cipher.DECRYPT_MODE, this.resultType.convert(ciphertext)),
            this.encoding);
      } else {
        throw new CipherException(Code.DECRYPTION_FAIL, this.getClass(),
            UNSUPPORTED_CIPHER_MODE, ciphertext);
      }
    } catch (UnsupportedEncodingException | NullPointerException | InvalidKeyException
        | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
      throw new CipherException(Code.DECRYPTION_FAIL, this.getClass(), e.getMessage(),
          ciphertext);
    }
  }

  private byte[] doFinal(int encryptMode, byte[] bytes)
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    this.cipher.init(encryptMode, this.key);
    return this.cipher.doFinal(bytes);
  }

  private byte[] doFinal(int encryptMode, byte[] iv, byte[] bytes) throws InvalidKeyException,
      IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
    this.cipher.init(encryptMode, this.key, new IvParameterSpec(iv));
    return this.cipher.doFinal(bytes);
  }

}
