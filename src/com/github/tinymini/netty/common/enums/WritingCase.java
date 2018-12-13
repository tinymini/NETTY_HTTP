package com.github.tinymini.netty.common.enums;

import org.springframework.util.StringUtils;

/**
 * 변슈명 규칙 변경
 * 
 * @author shkim
 *
 */
public enum WritingCase {
  /** Capitalize */
  Capitalize {
    @Override
    public String change(String... args) {
      StringBuffer sb = new StringBuffer();
      for (String arg : args) {
        sb.append(capitalize(arg));
      }
      return sb.toString();
    }
  },
  /** camelCase */
  camelCase {
    @Override
    public String change(String... args) {
      StringBuffer sb = new StringBuffer();
      boolean isStart = true;
      for (String arg : args) {
        if (StringUtils.hasText(arg)) {
          if (isStart) {
            sb.append(camelCase(arg));
          } else {
            sb.append(capitalize(arg));
          }
          isStart = false;
        }
      }
      return sb.toString();
    }
  },
  /** kebab-case */
  kebab_case {
    @Override
    public String change(String... args) {
      StringBuffer sb = new StringBuffer();
      char delim = '-';
      for (String arg : args) {
        sb.append(splitedCases(arg, false, delim));
        sb.append(delim);
      }
      sb.setLength(sb.length() - 1);
      return sb.toString();
    }
  },
  /** snake_case */
  snake_case {
    @Override
    public String change(String... args) {
      StringBuffer sb = new StringBuffer();
      char delim = '_';
      for (String arg : args) {
        sb.append(splitedCases(arg, false, delim));
        sb.append(delim);
      }
      sb.setLength(sb.length() - 1);
      return sb.toString();
    }
  },
  /** PASCAL_CASE */
  PASCAL_CASE {
    @Override
    public String change(String... args) {
      StringBuffer sb = new StringBuffer();
      char delim = '-';
      for (String arg : args) {
        sb.append(splitedCases(arg, true, delim));
        sb.append(delim);
      }
      sb.setLength(sb.length() - 1);
      return sb.toString();
    }
  };

  /**
   * case 변경 함수
   * 
   * @param args
   * @return
   */
  public abstract String change(String... args);

  /**
   * 분리형
   * 
   * @param arg
   * @param isUpperCase
   * @param delimeter
   * @return
   */
  private static String splitedCases(String arg, boolean isUpperCase, char delimeter) {
    StringBuffer sb = new StringBuffer();
    if (arg.length() == 0) {
      return "";
    }
    boolean isStart = true;

    for (int i = 0; i < arg.length(); i++) {
      char currentChar = arg.charAt(i);

      if (isEnd(currentChar)) {
        isStart = true;
        sb.append(delimeter);
        continue;
      }

      if (Character.isUpperCase(currentChar)) {
        sb.append(delimeter);
      }

      if (isStart) {
        isStart = false;
      }

      if (isUpperCase) {
        sb.append(Character.toUpperCase(currentChar));
      } else {
        sb.append(Character.toLowerCase(currentChar));
      }
    }
    return sb.toString();
  }

  /**
   * 첫글자 대문자
   * 
   * @param arg
   * @return
   */
  private static String capitalize(String arg) {
    StringBuffer sb = new StringBuffer();
    if (arg.length() == 0) {
      return "";
    }
    boolean isStart = true;
    for (int i = 0; i < arg.length(); i++) {
      char currentChar = arg.charAt(i);
      if (isEnd(currentChar)) {
        isStart = true;
        continue;
      }
      if (Character.isUpperCase(currentChar)) {
        isStart = true;
      }
      if (isStart) {
        char currentCharToUpperCase = Character.toUpperCase(currentChar);
        sb.append(currentCharToUpperCase);
        isStart = false;
      } else {
        char currentCharToLowerCase = Character.toLowerCase(currentChar);
        sb.append(currentCharToLowerCase);
      }
    }
    return sb.toString();
  }

  /**
   * 카멜 케이스
   * 
   * @param arg
   * @return
   */
  private static String camelCase(String arg) {
    StringBuffer sb = new StringBuffer();
    if (arg.length() == 0) {
      return "";
    }
    char firstChar = arg.charAt(0);
    char firstCharToUpperCase = Character.toLowerCase(firstChar);
    boolean isStart = false;
    sb.append(firstCharToUpperCase);
    for (int i = 1; i < arg.length(); i++) {
      char currentChar = arg.charAt(i);
      if (isEnd(currentChar)) {
        isStart = true;
        continue;
      }
      if (Character.isUpperCase(currentChar)) {
        isStart = true;
      }
      if (isStart) {
        char currentCharToUpperCase = Character.toUpperCase(currentChar);
        sb.append(currentCharToUpperCase);
        isStart = false;
      } else {
        char currentCharToLowerCase = Character.toLowerCase(currentChar);
        sb.append(currentCharToLowerCase);
      }
    }
    return sb.toString();
  }

  /**
   * 마지막 글자 체크
   * 
   * @param currentChar
   * @return
   */
  private static boolean isEnd(char currentChar) {
    return currentChar == ' ' || currentChar == '-' || currentChar == '_';
  }
}
