package com.github.tinymini.netty.common.cipher.enums;

/**
 * 파라메터 암호화 타입
 * 
 * @author shkim
 *
 */
public enum DecryptType {
	/** 모든 파라메터 각자 암호화 */
	ALL,
	/** 선택된 하나의 파라메터로 암호화 */
	CHUNK,
	/** 선택된 파라메터 암호화 */
	SELECT,
	/** 전체 파라메터 하나로 암호화 - mimeType : text/plain */
	WHOLE
}
