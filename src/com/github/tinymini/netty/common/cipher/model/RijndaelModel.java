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
 * Rijndael 암/복호화 모델
 * 
 * @author shkim
 * @since 2018-04-05
 *
 */
public final class RijndaelModel extends SymmetricKeyModel {
  public RijndaelModel(Map<String, String> map) {
    super();
    this.algorithm = Algorithm.RIJNDAEL.name();
    this.cipherMode = CipherMode.CBC.name();
    this.encoding = Encoding.UTF_8.name();
    this.paddingType = PaddingType.PKCS5Padding.name();
    this.keySize = 256;
    this.blockSize = 256;

    ClassUtils.autoComplete(this, map);

    this.key = KeyUtils.generate(map.get(KEY), DataType.valueOf(map.get(KEY_TYPE)), this.keySize);
    String iv = map.get(IV);
    if (CommonUtils.hasText(iv)) {
      this.iv = KeyUtils.generate(iv,
          CommonUtils.nvl(DataType.valueOf(map.get(IV_TYPE)), DataType.valueOf(map.get(KEY_TYPE))),
          this.blockSize);
    }
  }

}
