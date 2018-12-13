package com.github.tinymini.netty.common.cipher.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import com.github.tinymini.netty.common.Code;
import com.github.tinymini.netty.common.cipher.enums.CipherMode;
import com.github.tinymini.netty.common.cipher.enums.PaddingType;
import com.github.tinymini.netty.common.cipher.exception.CipherException;
import com.github.tinymini.netty.common.cipher.model.PublicKeyModel;
import com.github.tinymini.netty.common.util.CommonUtils;

/**
 * RSA 암호화 관련
 * 
 * @author shkim
 * @since 2018-02-05
 */
public final class RSAUtil extends PublicKeyCipherUtil implements CipherUtil {

  /** 암/복호화 인스턴스 */
  private Cipher cipher;
  /** 키 생성 유틸 */
  private KeyFactory keyFactory;

  public RSAUtil(PublicKeyModel pkm) throws NoSuchAlgorithmException, NoSuchPaddingException {
    super(pkm);
    String cipherMode = CommonUtils.nvl(pkm.getCipherMode(), CipherMode.ECB.name());
    String paddingType = CommonUtils.nvl(pkm.getPaddingType(), PaddingType.PKCS1Padding.name());
    this.cipher = CipherInstanceHelper.generate(this.algorithm, cipherMode, paddingType);
    this.keyFactory = KeyFactory.getInstance(this.algorithm);
    this.resultType = pkm.getResultType();
    this.encoding = pkm.getEncoding();
    this.publicKey = pkm.getPublicKey();
    this.privateKey = pkm.getPrivateKey();
  }

  @Override
  public String encrypt(String plaintext) {
    try {
      return this.resultType.convert(doFinal(Cipher.ENCRYPT_MODE,
          keyFactory.generatePublic(new X509EncodedKeySpec(this.publicKey)),
          plaintext.getBytes(this.encoding)));
    } catch (InvalidKeySpecException | UnsupportedEncodingException | RuntimeException
        | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
      throw new CipherException(Code.ENCRYPTION_FAIL, this.getClass(), e.getMessage(),
          plaintext);
    }
  }

  @Override
  public String decrypt(String ciphertext) {
    try {
      return new String(doFinal(Cipher.DECRYPT_MODE,
          keyFactory.generatePrivate(new PKCS8EncodedKeySpec(this.privateKey)),
          this.resultType.convert(ciphertext)), this.encoding);
    } catch (InvalidKeySpecException | UnsupportedEncodingException | RuntimeException
        | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
      throw new CipherException(Code.DECRYPTION_FAIL, this.getClass(), e.getMessage(),
          ciphertext);
    }
  }

  private byte[] doFinal(int encryptMode, Key key, byte[] bytes)
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    this.cipher.init(encryptMode, key);
    return this.cipher.doFinal(bytes);
  }

}
