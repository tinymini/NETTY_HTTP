package com.github.tinymini.netty.common.cipher.enums;

import javax.xml.bind.DatatypeConverter;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 * 데이터 타입
 * 
 * @author shkim
 *
 */
public enum DataType {
  /** hex */
  HEX {
    public String convert(byte[] bytes) {
      return DatatypeConverter.printHexBinary(bytes);
    }

    public byte[] convert(String string) {
      return DatatypeConverter.parseHexBinary(string);
    }
  },
  /** base64 */
  BASE64 {
    public String convert(byte[] bytes) {
      return Base64.encodeBase64String(bytes);
    }

    public byte[] convert(String string) {
      return Base64.decodeBase64(string);
    }
  };

  public abstract byte[] convert(String string);

  public abstract String convert(byte[] bytes);
}
