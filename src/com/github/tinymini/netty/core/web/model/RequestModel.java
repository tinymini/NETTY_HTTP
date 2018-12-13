package com.github.tinymini.netty.core.web.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.tinymini.netty.common.cipher.util.CipherUtil;
import com.github.tinymini.netty.common.util.LoggingUtils;
import com.github.tinymini.netty.web.enums.ParameterCipherType;
import com.github.tinymini.netty.web.enums.ParameterType;
import com.github.tinymini.netty.web.enums.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

/**
 * 요청 모델
 * 
 * @author shkim
 *
 */
public class RequestModel {
  /** 요청 URI */
  private URI uri;
  /** 메소드 타입 */
  private HttpMethod requestMethod;
  /** 파라메터 타입 */
  private ParameterType paramterType;
  /** 요청 쿠키 */
  private String cookie;
  /** 연걸 타임아웃 */
  private int connectionTimeout;
  /** 읽기 타임아웃 */
  private int readTimeout;
  /** 요청 파라메터 */
  private Map<String, List<String>> paramMap;
  /** 요청 헤더 */
  private Map<String, String> headerMap;
  /** 요청시 암호화 유틸 */
  private CipherUtil cipherUtil;

  @Override
  public String toString() {
    return "RequestModel [rawPath=" + uri.getRawPath() + ", requestMethod=" + requestMethod
        + ", paramterType=" + paramterType + ", cookie=" + cookie + ", connectionTimeout="
        + connectionTimeout + ", readTimeout=" + readTimeout + ", paramMap="
        + LoggingUtils.paramMapToString(paramMap) + ", headerMap="
        + LoggingUtils.paramMapToString(headerMap) + "]";
  }

  /**
   * 스트링 URL 생성자
   * 
   * @param requestUrl
   */
  public RequestModel(String rawPath) {
    this.paramMap = new HashMap<>();
    this.headerMap = new HashMap<>();
    try {
      this.uri = new URI(rawPath);
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException("Invalid URI");
    }
  }

  public String getRawPath() {
    return uri.getRawPath();
  }

  public int getPort() {
    int port = this.uri.getPort();
    if (port < 0) {
      port = Protocol.valueOf(this.getScheme().toUpperCase()).getDefaultPort();
    }
    return port;
  }

  public String getHost() {
    return this.uri.getHost();
  }

  public String getScheme() {
    return this.uri.getScheme();
  }

  public HttpMethod getRequestMethod() {
    return requestMethod;
  }

  public void setRequestMethod(HttpMethod requestMethod) {
    this.requestMethod = requestMethod;
  }

  public ParameterType getParamterType() {
    return paramterType;
  }

  public void setParamterType(ParameterType paramterType) {
    this.paramterType = paramterType;
  }

  public String getCookie() {
    return cookie;
  }

  public void setCookie(String cookie) {
    this.cookie = cookie;
  }

  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }

  public Map<String, List<String>> getParamMap() {
    return paramMap;
  }

  public void setParamMap(Map<String, List<String>> paramMap) {
    this.paramMap = paramMap;
  }

  public Map<String, String> getHeaderMap() {
    return headerMap;
  }

  public void setHeaderMap(Map<String, String> headerMap) {
    this.headerMap = headerMap;
  }

  public CipherUtil getCipherUtil() {
    return cipherUtil;
  }

  public void setCipherUtil(CipherUtil cipherUtil) {
    this.cipherUtil = cipherUtil;
  }

  public RequestModel addParam(String paramKey, Object paramValue) {
    String value = String.valueOf(paramValue);
    List<String> parameter;
    if (this.paramMap.containsKey(paramKey)) {
      parameter = this.paramMap.get(paramKey);
    } else {
      parameter = new ArrayList<>();
    }
    parameter.add(value);
    this.paramMap.put(paramKey, parameter);
    return this;
  }

  public String getParam(String paramKey) {
    return this.paramMap.get(paramKey).get(0);
  }

  public RequestModel addHeader(String headerKey, Object headerValue) {
    this.headerMap.put(headerKey, String.valueOf(headerValue));
    return this;
  }

  public String getHeaderParam(String headerKey) {
    return headerMap.get(headerKey);
  }

  public String encode() {
    return this.paramterType.encode(this.paramMap);
  }

  public RequestModel encrypt(ParameterCipherType cipherType) {
    return encrypt(cipherType, this.paramterType);
  }

  public RequestModel encrypt(ParameterCipherType cipherType, ParameterType encryptedType,
      String... keys) {
    if (this.cipherUtil == null) {
      throw new NullPointerException("CipherUtil is Null");
    }
    this.paramMap = cipherType.encryptToMap(cipherUtil, encryptedType, this.paramMap, keys);
    return this;
  }

  public FullHttpRequest getHttpRequest() {
    FullHttpRequest request =
        new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, this.requestMethod, getRawPath());
    // 헤더 설정
    request.headers().add(HttpHeaderNames.CONTENT_TYPE, this.paramterType.getApplicationType());
    request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
    request.headers().set(HttpHeaderNames.HOST, getHost());
    request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
    // http 바디 설정
    ByteBuf bbuf = Unpooled.copiedBuffer(encode(), StandardCharsets.UTF_8);
    request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
    request.content().clear().writeBytes(bbuf);
    return request;
  }

}
