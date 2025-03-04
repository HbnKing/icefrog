package com.whaleal.icefrog.extra.compress;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 压缩解压异常语言异常
 *
 * @author Looly
 * @author wh
 */
public class CompressException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CompressException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public CompressException(String message) {
		super(message);
	}

	public CompressException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public CompressException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public CompressException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public CompressException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
