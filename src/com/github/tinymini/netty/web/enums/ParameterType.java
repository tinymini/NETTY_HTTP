package com.github.tinymini.netty.web.enums;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import com.github.tinymini.netty.web.util.ParameterUtils;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.QueryStringEncoder;

/**
 * 파라메터 타입
 * 
 * @author shkim
 *
 */
public enum ParameterType {
  /** key=value */
  QUERY_STRING("application/x-www-form-urlencoded") {
    @Override
    public String encode(Map<String, List<String>> parameterMap) {
      QueryStringEncoder qse = new QueryStringEncoder("");
      for (Map.Entry<String, List<String>> parameter : parameterMap.entrySet()) {
        String key = parameter.getKey();
        for (String value : parameter.getValue()) {
          qse.addParam(key, value);
        }
      }
      return qse.toString().substring(1);
    }

    @Override
    public Map<String, List<String>> decode(String parameterSource) {
      return new QueryStringDecoder(parameterSource, false).parameters();
    }
  },
  /** {key:value} */
  JSON("application/json") {
    @Override
    public String encode(Map<String, List<String>> parameterMap) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        return mapper.writeValueAsString(parameterMap);
      } catch (IOException e) {
        logger.info(e.getMessage());
        throw new RuntimeException("Fail to parse");
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, List<String>> decode(String parameterSource) {
      ObjectMapper mapper = new ObjectMapper();
      Map<String, List<String>> resultMap = new HashMap<>();
      Map<String, Object> paramMap = null;
      try {
        paramMap = mapper.readValue(parameterSource, Map.class);
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
          ParameterUtils.putParameterList(resultMap, entry.getKey(), entry.getValue());
        }
        return resultMap;
      } catch (IOException e) {
        logger.info(e.getMessage());
        throw new RuntimeException("Fail to serialize");
      }
    }
  };
  
  protected final Log logger = LogFactory.getLog(getClass());
  
  ParameterType(String applicationType) {
    this.applicationType = applicationType;
  }

  /**
   * 파라메터 맵 -> 스트링
   * 
   * @param parameterMap
   * @return
   */
  abstract public String encode(Map<String, List<String>> parameterMap);

  /**
   * 스트링 -> 파라메터 맵
   * 
   * @param parameterSource
   * @return
   */
  abstract public Map<String, List<String>> decode(String parameterSource);

  private String applicationType;

  public String getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(String applicationType) {
    this.applicationType = applicationType;
  }


}
