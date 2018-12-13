package com.github.tinymini.netty.common.enums;

/**
 * 인코딩
 * 
 * @author shkim
 *
 */
public enum Encoding {
  UTF_8("UTF-8"), EUC_KR("EUC-KR"), ISO_8859_1("ISO-8859-1");
  private String name;

  Encoding(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
