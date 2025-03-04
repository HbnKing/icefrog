package com.whaleal.icefrog.http;

import com.whaleal.icefrog.core.util.StrUtil;

/**
 * HTTP异常
 *
 * @author Looly
 * @author wh
 */
public class HttpException extends RuntimeException {
	private static final long serialVersionUID = 8247610319171014183L;

	public HttpException(Throwable e) {
		super(e.getMessage(), e);
	}

	public HttpException(String message) {
		super(message);
	}

	public HttpException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public HttpException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public HttpException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public HttpException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
