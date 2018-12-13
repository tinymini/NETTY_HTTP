package com.github.tinymini.netty.web.enums;

/**
 * 웹 프로토콜
 * 
 * @author shkim
 *
 */
public enum Protocol {
  /** http */
  HTTP(80),
  /** https */
  HTTPS(443);

  private String suffix = "://";
  private int defaultPort;

  Protocol(int defaultPort) {
    this.defaultPort = defaultPort;
  }

  /**
   * https:// 형식으로 리턴
   * 
   * @return
   */
  public String getProtocolWithSuffix() {
    return this.name().toLowerCase() + suffix;
  }

  public int getDefaultPort() {
    return defaultPort;
  }
}
