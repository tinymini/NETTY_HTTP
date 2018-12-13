package com.github.tinymini.netty.common.cipher.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import com.github.tinymini.netty.common.cipher.enums.Algorithm;
import com.github.tinymini.netty.common.cipher.enums.DataType;
import com.github.tinymini.netty.common.enums.ExceptionMessage;

/**
 * 키, IV 생성
 * 
 * @author shkim
 * @since 2018-04-03
 */
public final class KeyUtils {
  /**
   * 생성자 숨김 & 생성 불가
   */
  private KeyUtils() {
    throw new IllegalStateException(ExceptionMessage.NOT_INSTANTIABLE.msg());
  }

  /**
   * 바이트를 설정된 사이즈로 맞춰서 세팅함
   * 
   * @param param 입력 바이트
   * @param size 설정 크기
   * @return
   */
  public static byte[] adjustBlockSize(byte[] param, int size) {
    int byteSize = size / Byte.SIZE;
    byte[] bytes = new byte[byteSize];
    System.arraycopy(param, 0, bytes, 0, Math.min(byteSize, param.length));
    return bytes;
  }

  /**
   * 대칭 키 생성
   * 
   * @param key 스트링 키
   * @param dataType 키 데이터 타입
   * @param keySize 키 사이즈
   * @return
   */
  public static byte[] generate(String key, DataType dataType, int keySize) {
    return adjustBlockSize(dataType.convert(key), keySize);
  }

  /**
   * 대칭 키 생성
   * 
   * @param keyInstance 인스턴스명
   * @param passPhrase 암호
   * @param salt 키 생성 솔트값
   * @param saltDataType 솔트 데이터 타입
   * @param keySize 키 사이즈
   * @param iterationCount 반복 횟수
   * @return
   * @throws InvalidKeySpecException
   * @throws NoSuchAlgorithmException
   */
  public static byte[] generate(String keyInstance, String passPhrase, String salt,
      DataType saltDataType, int keySize, int iterationCount)
      throws InvalidKeySpecException, NoSuchAlgorithmException {
    return (SecretKeyFactory.getInstance(keyInstance))
        .generateSecret(new PBEKeySpec(passPhrase.toCharArray(), saltDataType.convert(salt),
            iterationCount, keySize))
        .getEncoded();
  }

  /**
   * 공개 키/ 개인키 생성
   * 
   * @param keySize
   * @return
   * @throws NoSuchAlgorithmException
   */
  public static KeyPair generate(int keySize) throws NoSuchAlgorithmException {
    KeyPairGenerator generator = KeyPairGenerator.getInstance(Algorithm.RSA.name());
    generator.initialize(keySize, new SecureRandom());
    return generator.generateKeyPair();
  }
}
