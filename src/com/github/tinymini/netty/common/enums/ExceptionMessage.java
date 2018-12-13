package com.github.tinymini.netty.common.enums;

/**
 * 익셉션용 메세지
 * 
 * @author shkim
 *
 */
public enum ExceptionMessage {

  /** 인스턴스 생성 불가 */
  NOT_INSTANTIABLE("This Class is Not instantiable"),
  /** 메소드 사용 금지 */
  UNUSE_METHOD("Do not use this method");


  ExceptionMessage(String msg) {
    this.msg = msg;
  }

  private String msg;

  public String msg() {
    return this.msg;
  }

}
