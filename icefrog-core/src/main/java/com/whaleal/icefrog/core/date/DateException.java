package com.whaleal.icefrog.core.date;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 工具类异常
 * @author Looly
 * @author wh
 */
public class DateException extends RuntimeException{
	private static final long serialVersionUID = 8247610319171014183L;

	public DateException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public DateException(String message) {
		super(message);
	}

	public DateException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public DateException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public DateException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
