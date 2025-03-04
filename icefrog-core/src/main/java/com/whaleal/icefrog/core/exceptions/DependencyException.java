package com.whaleal.icefrog.core.exceptions;

import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 依赖异常
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class DependencyException extends RuntimeException {
	private static final long serialVersionUID = 8247610319171014183L;

	public DependencyException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public DependencyException(String message) {
		super(message);
	}

	public DependencyException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public DependencyException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public DependencyException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public DependencyException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
