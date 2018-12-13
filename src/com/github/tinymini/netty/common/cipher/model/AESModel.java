package com.github.tinymini.netty.common.cipher.model;

import java.util.Map;
import com.github.tinymini.netty.common.cipher.enums.Algorithm;
import com.github.tinymini.netty.common.cipher.enums.CipherMode;
import com.github.tinymini.netty.common.cipher.enums.DataType;
import com.github.tinymini.netty.common.cipher.enums.PaddingType;
import com.github.tinymini.netty.common.cipher.util.KeyUtils;
import com.github.tinymini.netty.common.enums.Encoding;
import com.github.tinymini.netty.common.util.ClassUtils;
import com.github.tinymini.netty.common.util.CommonUtils;

/**
 * AES 암/복호화 모델
 * 
 * @author shkim
 * @since 2018-04-05
 *
 */
public final class AESModel extends SymmetricKeyModel {
  public AESModel(Map<String, String> map) {
    super();
    this.algorithm = Algorithm.AES.name();
    this.cipherMode = CipherMode.ECB.name();
    this.encoding = Encoding.UTF_8.name();
    this.paddingType = PaddingType.PKCS5Padding.name();
    this.keySize = 128;
    this.blockSize = 128;

    ClassUtils.autoComplete(this, map);

    this.key = KeyUtils.generate(map.get(KEY), DataType.valueOf(map.get(KEY_TYPE)), this.keySize);
    String iv = map.get(IV);
    if (CommonUtils.hasText(iv)) {
      this.iv = KeyUtils.generate(iv,
          CommonUtils.nvl(DataType.valueOf(map.get(KEY_TYPE)), DataType.valueOf(map.get(IV_TYPE))),
          this.blockSize);
    }
  }
}
