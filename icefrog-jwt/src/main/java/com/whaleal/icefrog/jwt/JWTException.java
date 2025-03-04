package com.whaleal.icefrog.jwt;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * JWT异常
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class JWTException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JWTException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public JWTException(String message) {
		super(message);
	}

	public JWTException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public JWTException(String message, Throwable cause) {
		super(message, cause);
	}

	public JWTException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public JWTException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
