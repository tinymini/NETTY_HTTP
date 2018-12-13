package com.github.tinymini.netty.web.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.tinymini.netty.common.cipher.util.CipherUtil;
import com.github.tinymini.netty.web.util.ParameterUtils;

/**
 * 파라메터 암호화 타입
 * 
 * @author shkim
 *
 */
public enum ParameterCipherType {
  /** 모든 파라메터 각자 암호화 */
  ALL {

    @Override
    public Map<String, List<String>> encryptToMap(CipherUtil cipherUtil, ParameterType sourceType,
        Map<String, List<String>> parameterMap, String... keys) {
      Map<String, List<String>> resultMap = new HashMap<>();

      for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
        resultMap.put(entry.getKey(), doFinalArray(entry.getValue(), cipherUtil, true));
      }

      return resultMap;
    }

    @Override
    public Map<String, List<String>> decryptFromMap(CipherUtil cipherUtil, ParameterType sourceType,
        Map<String, List<String>> parameterMap, String... keys) {
      Map<String, List<String>> resultMap = new HashMap<>();

      for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
        resultMap.put(entry.getKey(), doFinalArray(entry.getValue(), cipherUtil, false));
      }

      return resultMap;
    }

  },
  /** 선택된 하나의 파라메터로 암호화 */
  CHUNK {

    /**
     * key 첫번재 파라메터는 묶어서 내보낼 키 이름, 두번째 이후로는 선택
     */
    @Override
    public Map<String, List<String>> encryptToMap(CipherUtil cipherUtil, ParameterType sourceType,
        Map<String, List<String>> parameterMap, String... keys) {
      if (keys == null || keys.length < 1) {
        throw new NullArgumentException("Argument is Invalid");
      }
      Map<String, List<String>> resultMap = new HashMap<>();
      Map<String, List<String>> partMap = new HashMap<>();

      try {
        boolean isAll = keys.length == 1;
        List<String> list = Arrays.asList(keys);
        String paramKeyName = keys[0];

        for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
          String key = entry.getKey();
          List<String> value = entry.getValue();
          if (list.contains(key) || isAll) {
            partMap.put(key, value);
          } else {
            resultMap.put(key, value);
          }
        }
        ParameterUtils.putParameterList(resultMap, paramKeyName,
            cipherUtil.encrypt(sourceType.encode(partMap)));
      } catch (Exception e) {
        logger.info(e.getMessage());
      }
      return resultMap;
    }

    @Override
    public Map<String, List<String>> decryptFromMap(CipherUtil cipherUtil, ParameterType sourceType,
        Map<String, List<String>> parameterMap, String... keys) {
      if (keys == null || keys.length < 1) {
        throw new NullArgumentException("Argument is Invalid");
      }
      Map<String, List<String>> resultMap = new HashMap<>();
      List<String> list = Arrays.asList(keys);

      for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
        String key = entry.getKey();
        List<String> value = entry.getValue();
        if (list.contains(key)) {
          resultMap
              .putAll(sourceType.decode(doFinalArray(entry.getValue(), cipherUtil, false).get(0)));
        } else {
          resultMap.put(key, value);
        }
      }

      return resultMap;
    }

  },
  /** 선택된 파라메터 암호화 */
  SELECT {

    @Override
    public Map<String, List<String>> encryptToMap(CipherUtil cipherUtil, ParameterType sourceType,
        Map<String, List<String>> parameterMap, String... keys) {
      if (keys == null || keys.length < 1) {
        throw new NullArgumentException("Argument is Invalid");
      }
      Map<String, List<String>> resultMap = new HashMap<>();

      try {
        List<String> list = Arrays.asList(keys);
        for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
          String key = entry.getKey();
          List<String> value = entry.getValue();
          if (list.contains(key)) {
            resultMap.put(key, doFinalArray(entry.getValue(), cipherUtil, true));
          } else {
            resultMap.put(key, value);
          }
        }
      } catch (Exception e) {
        logger.info(e.getMessage());
      }
      return resultMap;
    }

    @Override
    public Map<String, List<String>> decryptFromMap(CipherUtil cipherUtil, ParameterType sourceType,
        Map<String, List<String>> parameterMap, String... keys) {
      // TODO Auto-generated method stub
      return null;
    }

  },
  /** 전체 파라메터 하나로 암호화 - mimeType : text/plain */
  WHOLE {

    @Override
    public String encryptToString(CipherUtil cipherUtil, ParameterType sourceType,
        Map<String, List<String>> parameterMap) {

      // ParameterUtils.paramToJson(parameterMap).toString();
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public Map<String, List<String>> decryptFromText(CipherUtil cipherUtil,
        ParameterType sourceType, String cipheredText, String... keys) {
      // TODO Auto-generated method stub
      return null;
    }

  };
  protected final Log logger = LogFactory.getLog(getClass());
  
  /**
   * 타입에 따른 암호화
   * @param cipherUtil
   * @param sourceType
   * @param parameterMap
   * @param keys
   * @return
   */
  public Map<String, List<String>> encryptToMap(CipherUtil cipherUtil, ParameterType sourceType,
      Map<String, List<String>> parameterMap, String... keys) {
    throw new RuntimeException("need to override");
  }

  /**
   * 타입에 따른 암호화
   * @param cipherUtil
   * @param sourceType
   * @param parameterMap
   * @return
   */
  public String encryptToString(CipherUtil cipherUtil, ParameterType sourceType,
      Map<String, List<String>> parameterMap) {
    throw new RuntimeException("need to override");
  }

  /**
   * 타입에 따른 복호화
   * @param cipherUtil
   * @param sourceType
   * @param parameterMap
   * @param keys
   * @return
   */
  public Map<String, List<String>> decryptFromMap(CipherUtil cipherUtil, ParameterType sourceType,
      Map<String, List<String>> parameterMap, String... keys) {
    throw new RuntimeException("need to override");
  }


  /**
   * 타입에 따른 복호화
   * @param cipherUtil
   * @param sourceType
   * @param cipheredText
   * @param keys
   * @return
   */
  public Map<String, List<String>> decryptFromText(CipherUtil cipherUtil, ParameterType sourceType,
      String cipheredText, String... keys) {
    throw new RuntimeException("need to override");
  }

  /**
   * 배열 암/복호화 래퍼
   * @param values
   * @param cipherUtil
   * @param isEncrypt
   * @return
   */
  private static List<String> doFinalArray(List<String> values, CipherUtil cipherUtil,
      boolean isEncrypt) {
    if (values == null || cipherUtil == null) {
      throw new NullArgumentException("Arguments are null");
    }
    if (isEncrypt) {
      return encryptArray(values, cipherUtil);
    } else {
      return decryptArray(values, cipherUtil);
    }
  }

  /**
   * 배열 암호화
   * @param values
   * @param cipherUtil
   * @return
   */
  private static List<String> encryptArray(List<String> values, CipherUtil cipherUtil) {
    String even = null;
    int size = values.size();
    List<String> returnValues = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      even = values.get(i);
      if (even != null) {
        returnValues.add(i, cipherUtil.encrypt(even));
      }
    }
    return returnValues;
  }

  /**
   * 배열 복호화
   * @param values
   * @param cipherUtil
   * @return
   */
  private static List<String> decryptArray(List<String> values, CipherUtil cipherUtil) {
    String even = null;
    int size = values.size();
    List<String> returnValues = new ArrayList<>(size);
    for (int i = 0; i < values.size(); i++) {
      even = values.get(i);
      if (even != null) {
        returnValues.add(i, cipherUtil.decrypt(even));
      }
    }
    return returnValues;
  }

}
