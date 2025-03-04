package com.whaleal.icefrog.jwt;

import java.util.Map;

/**
 * JWT头部信息
 *
 * @author Looly 
 * @author wh
 * @since 1.0.0
 */
public class JWTHeader extends Claims {
	private static final long serialVersionUID = 1L;

	//Header names
	/**
	 * 加密算法，通常为HMAC SHA256（HS256）
	 */
	public static final String ALGORITHM = "alg";
	/**
	 * 声明类型，一般为jwt
	 */
	public static final String TYPE = "typ";
	/**
	 * 内容类型（content type）
	 */
	public static final String CONTENT_TYPE = "cty";
	/**
	 * jwk的ID编号
	 */
	public static final String KEY_ID = "kid";

	/**
	 * 构造，初始化默认(typ=JWT)
	 */
	public JWTHeader() {
		setClaim(TYPE, "JWT");
	}

	/**
	 * 增加“kid”头信息
	 *
	 * @param keyId kid
	 * @return this
	 */
	public JWTHeader setKeyId(String keyId) {
		setClaim(KEY_ID, keyId);
		return this;
	}

	/**
	 * 增加自定义JWT认证头
	 *
	 * @param headerClaims 头信息
	 * @return this
	 */
	public JWTHeader addHeaders(Map<String, ?> headerClaims) {
		putAll(headerClaims);
		return this;
	}
}
