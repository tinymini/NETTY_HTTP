package com.github.tinymini.netty.common.cipher.model;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import com.github.tinymini.netty.common.cipher.enums.Algorithm;
import com.github.tinymini.netty.common.cipher.enums.CipherMode;
import com.github.tinymini.netty.common.cipher.enums.DataType;
import com.github.tinymini.netty.common.cipher.enums.PaddingType;
import com.github.tinymini.netty.common.cipher.exception.CipherException;
import com.github.tinymini.netty.common.cipher.util.KeyUtils;
import com.github.tinymini.netty.common.enums.Encoding;
import com.github.tinymini.netty.common.util.ClassUtils;
import com.github.tinymini.netty.common.util.CommonUtils;

/**
 * RSA 암/복호화 모델
 * 
 * @author shkim
 * @since 2018-0405
 */
public final class RSAModel extends PublicKeyModel {
  public RSAModel(Map<String, String> map) {
    super();
    this.algorithm = Algorithm.RSA.name();
    this.cipherMode = CipherMode.ECB.name();
    this.encoding = Encoding.UTF_8.name();
    this.paddingType = PaddingType.PKCS1Padding.name();
    this.resultType = DataType.HEX;
    this.keySize = 2048;

    ClassUtils.autoComplete(this, map);

    String publicKey = map.get(PUBLIC_KEY);
    String privateKey = map.get(PRIVATE_KEY);
    DataType dataType = DataType.valueOf(map.get(KEY_TYPE));
    boolean isGenerate = true;
    if (CommonUtils.hasText(publicKey)) {
      setPublicKey(dataType.convert(publicKey));
      isGenerate = false;
    }
    if (CommonUtils.hasText(privateKey)) {
      setPrivateKey(dataType.convert(privateKey));
      isGenerate = false;
    }
    try {
      if (isGenerate) {
        setKeyPair(KeyUtils.generate(this.keySize));
      }
    } catch (NoSuchAlgorithmException e) {
      throw new CipherException(this.getClass(), e.getMessage());
    }
  }
}
