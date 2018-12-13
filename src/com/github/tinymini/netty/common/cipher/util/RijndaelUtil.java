package com.github.tinymini.netty.common.cipher.util;

import java.io.UnsupportedEncodingException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import com.github.tinymini.netty.common.Code;
import com.github.tinymini.netty.common.cipher.enums.CipherMode;
import com.github.tinymini.netty.common.cipher.enums.PaddingType;
import com.github.tinymini.netty.common.cipher.exception.CipherException;
import com.github.tinymini.netty.common.cipher.model.SymmetricKeyModel;

public final class RijndaelUtil extends AbstractSymmetricKeyCipherUtil {
  private CipherParameters cipherParameters;
  private PaddedBufferedBlockCipher paddedBufferedBlockCipher;

  public RijndaelUtil(SymmetricKeyModel skm) {
    super(skm);
    this.iv = skm.getIv();
    String paddingType = skm.getPaddingType();
    BlockCipher rijndael = new RijndaelEngine(this.blockSize);
    BlockCipherPadding padding = PaddingType.valueOf(paddingType).getPadding();
    if (this.cipherMode.equals(CipherMode.CBC.name())) {
      this.cipherParameters = new ParametersWithIV(new KeyParameter(this.byteKey), this.iv);
      this.paddedBufferedBlockCipher =
          new PaddedBufferedBlockCipher(new CBCBlockCipher(rijndael), padding);
    } else if (this.cipherMode.equals(CipherMode.ECB.name())) {
      this.cipherParameters = new KeyParameter(this.byteKey);
      this.paddedBufferedBlockCipher = new PaddedBufferedBlockCipher(rijndael, padding);
    }
  }

  @Override
  public String encrypt(String plaintext) {
    try {
      this.paddedBufferedBlockCipher.init(true, this.cipherParameters);
      return this.resultType
          .convert(doFinal(this.paddedBufferedBlockCipher, plaintext.getBytes(this.encoding)));
    } catch (UnsupportedEncodingException | DataLengthException | IllegalStateException
        | InvalidCipherTextException e) {
      throw new CipherException(Code.ENCRYPTION_FAIL, this.getClass(), e.getMessage(),
          plaintext);
    }
  }

  @Override
  public String decrypt(String ciphertext) {
    try {
      this.paddedBufferedBlockCipher.init(false, this.cipherParameters);
      return new String(
          doFinal(this.paddedBufferedBlockCipher, this.resultType.convert(ciphertext)),
          this.encoding);
    } catch (DataLengthException | IllegalStateException | UnsupportedEncodingException
        | InvalidCipherTextException e) {
      throw new CipherException(Code.DECRYPTION_FAIL, this.getClass(), e.getMessage(),
          ciphertext);
    }
  }

  public byte[] doFinal(PaddedBufferedBlockCipher cipher, byte[] data)
      throws DataLengthException, IllegalStateException, InvalidCipherTextException {
    int minSize = cipher.getOutputSize(data.length);
    byte[] output = new byte[minSize];
    int length1 = cipher.processBytes(data, 0, data.length, output, 0);
    int length2 = cipher.doFinal(output, length1);
    int actualLength = length1 + length2;
    byte[] cipherArray = new byte[actualLength];
    for (int x = 0; x < actualLength; x++) {
      cipherArray[x] = output[x];
    }
    return cipherArray;
  }

}
