package com.whaleal.icefrog.core.comparator;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 比较异常
 *
 * @author Looly
 * @author wh
 */
public class ComparatorException extends RuntimeException {
	private static final long serialVersionUID = 4475602435485521971L;

	public ComparatorException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public ComparatorException(String message) {
		super(message);
	}

	public ComparatorException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public ComparatorException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public ComparatorException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
