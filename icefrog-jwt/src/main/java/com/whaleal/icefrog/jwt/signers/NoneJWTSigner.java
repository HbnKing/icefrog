package com.whaleal.icefrog.jwt.signers;

import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 无需签名的JWT签名器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class NoneJWTSigner implements JWTSigner {

	public static final String ID_NONE = "none";

	public static final NoneJWTSigner NONE = new NoneJWTSigner();

	@Override
	public String sign(String headerBase64, String payloadBase64) {
		return StrUtil.EMPTY;
	}

	@Override
	public boolean verify(String headerBase64, String payloadBase64, String signBase64) {
		return StrUtil.isEmpty(signBase64);
	}

	@Override
	public String getAlgorithm() {
		return ID_NONE;
	}
}
