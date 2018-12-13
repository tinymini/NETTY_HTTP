package com.github.tinymini.netty.common;

/**
 * 코드
 * 
 * @author shkim
 *
 */
public interface Code {

  /** 성공 */
  public static int SUCCESS = 0;
  /** 실패 */
  public static int FAIL = -1;
  /** 필드 데이터 부적합 */
  public static int INVALID_FIELD_DATA = -2;
  /** 형변환 실패 */
  public static int CASTING_FAIL = -3;
  /** 빈 없음 */
  public static int NO_BEAN = -4;
  /** DB 처리 실패 */
  public static int DB_ERROR = -5;
  /** 정의되지 않은 에러 */
  public static int UNKNOWN_ERROR = -9999;
  
  
  /** 존재하지 않는 API */
  public static int API_NOT_EXIST = -1400;
  /** 메소드 타입 불일치 */
  public static int INVALID_API_METHOD = -1401;
  /** 파라메터 에러 */
  public static int INVALID_API_PARAMETER = -1402;
  
  /** 암/복호화 실패 */
  public static int CIPHER_FAIL = -1500;
  /** 암호화 실패 */
  public static int ENCRYPTION_FAIL = -1501;
  /** 복호화 실패 */
  public static int DECRYPTION_FAIL = -1502;

}
