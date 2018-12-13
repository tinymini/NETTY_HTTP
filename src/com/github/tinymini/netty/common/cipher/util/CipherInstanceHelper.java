package com.github.tinymini.netty.common.cipher.util;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import com.github.tinymini.netty.common.util.BeanUtils;
import kr.co.tvhub.common.cipher.util.CipherUtil;

/**
 * 암호화 인스턴스 생성 빈 관리
 * 
 * @author shkim
 * @since 2018-04-03
 */
public final class CipherInstanceHelper {
  /**
   * 생성자 숨김 & 생성 불가
   */
  private CipherInstanceHelper() {
    throw new IllegalStateException("Not instantiable");
  }

  private static final String DELIMITER = "/";

  /**
   * 인스턴스 생성
   * 
   * @param algorithm
   * @param cipherMode
   * @param paddingType
   * @return
   * @throws NoSuchAlgorithmException
   * @throws NoSuchPaddingException
   */
  public static Cipher generate(String algorithm, String cipherMode, String paddingType)
      throws NoSuchAlgorithmException, NoSuchPaddingException {
    StringBuffer instanceName = new StringBuffer();
    instanceName.append(algorithm).append(DELIMITER).append(cipherMode).append(DELIMITER)
        .append(paddingType);
    return Cipher.getInstance(instanceName.toString());
  }

  /**
   * 암호화 유틸 (빈) 얻기
   * 
   * @param name {name}CipherUtil 명으로 등록된 빈 반환
   * @return
   */
  public static CipherUtil getCipher(String name) {
    return BeanUtils.getBean(name.toLowerCase() + "CipherUtil", CipherUtil.class);
  };

}
