package com.whaleal.icefrog.core.bean;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * Bean异常
 * @author Looly
 * @author wh
 */
public class BeanException extends RuntimeException{
	private static final long serialVersionUID = -8096998667745023423L;

	public BeanException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public BeanException(String message) {
		super(message);
	}

	public BeanException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public BeanException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public BeanException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
