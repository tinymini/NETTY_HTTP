package com.github.tinymini.netty.core.web.model;


import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.util.StringUtils;
import com.github.tinymini.netty.common.cipher.util.CipherUtil;
import com.github.tinymini.netty.common.util.CommonUtils;

/**
 * 응답 모델
 * 
 * @author shkim
 *
 */
public final class ResponseModel {
  /** 응답 코드 */
  private int responseCode;
  /** 응답 내용 */
  private String responseBody;
  /** 응답 쿠키 */
  private String cookie;
  /** 응답시 암호화 유틸 */
  private CipherUtil cipherUtil;

  public int getResponseCode() {
    return responseCode;
  }

  public ResponseModel setResponseCode(int responseCode) {
    this.responseCode = responseCode;
    return this;
  }

  public String getResponseBody() {
    return responseBody;
  }

  public ResponseModel setResponseBody(String responseBody) {
    this.responseBody = responseBody;
    return this;
  }

  public String getCookie() {
    return cookie;
  }

  public ResponseModel setCookie(String cookie) {
    this.cookie = cookie;
    return this;
  }

  /**
   * 암호화 유틸을 얻는다
   * 
   * @return
   */
  public CipherUtil getCipherUtil() {
    return cipherUtil;
  }

  /**
   * 암호화 유틸을 설정한다
   * 
   * @param cipherUtil
   */
  public ResponseModel setCipherUtil(CipherUtil cipherUtil) {
    this.cipherUtil = cipherUtil;
    return this;
  }

  /**
   * 제이슨 형식 리턴
   * 
   * @return
   */
  public JSONObject getResponseJson() {
    JSONObject responseObject = new JSONObject();
    if (StringUtils.hasLength(this.responseBody)) {
      responseObject = (JSONObject) JSONValue.parse(new StringReader(this.responseBody));
    }
    return responseObject;
  }

  @SuppressWarnings("unchecked")
  public JSONObject getDecryptedJson(String... keys) {
    JSONObject jsonObject = getResponseJson();
    jsonObject.entrySet();

    if (this.cipherUtil != null) {
      if (keys.length > 0) {
        for (int i = 0; i < keys.length; i++) {
          String key = keys[i];
          Object value = jsonObject.get(key);
          if (value instanceof String) {
            jsonObject.put(key, this.cipherUtil.decrypt((String) value));
          }
        }
      } else {
        for (Object obj : jsonObject.entrySet()) {
          Map.Entry<?, ?> e = (Map.Entry<?, ?>) obj;
          String key = (String) e.getKey();
          Object value = e.getValue();
          if (value instanceof String) {
            jsonObject.put(key, this.cipherUtil.decrypt((String) value));
          }
          if (value instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) value;
            for (int i = 0; i < jsonArray.size(); i++) {
              String even = (String) jsonArray.get(i);
              if (CommonUtils.hasText(even)) {
                jsonArray.set(i, this.cipherUtil.decrypt((String) jsonArray.get(i)));
              }
            }
            jsonObject.put(key, jsonArray);
          }
        }
      }
    }
    return jsonObject;
  }

  /**
   * 맵 형식 리턴
   * 
   * @return
   */
  public Map<String, String> getResponseMap() {
    String[] arr = null;
    String[] innerArr = null;
    Map<String, String> map = new HashMap<>();
    if (this.responseBody != null) {
      arr = this.responseBody.split("&");
      if (arr.length > 0) {
        for (String even : arr) {
          innerArr = even.split("=");
          if (innerArr.length > 0) {
            map.put(innerArr[0], innerArr[1]);
          }
        }
      }
    }
    return map;
  }

  /**
   * 기본형 파라메터 전체를 복호화 하여 맵으로 리턴한다
   * 
   * @return
   */
  public Map<String, String> getDecryptedMap(String... keys) {
    Map<String, String> map = getResponseMap();
    if (this.cipherUtil != null) {
      if (keys.length > 0) {
        for (int i = 0; i < keys.length; i++) {
          String key = keys[i];
          map.put(key, this.cipherUtil.encrypt(map.get(key)));
        }
      } else {
        for (Map.Entry<String, String> entry : map.entrySet()) {
          map.put(entry.getKey(), this.cipherUtil.encrypt(entry.getValue()));
        }
      }
    }
    return map;
  }

  @Override
  public String toString() {
    return "ResponseModel [responseCode=" + responseCode + ", responseBody=" + responseBody
        + ", cookie=" + cookie + "]";
  }
}
