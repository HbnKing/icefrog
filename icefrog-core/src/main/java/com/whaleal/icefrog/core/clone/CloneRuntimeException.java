package com.whaleal.icefrog.core.clone;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 克隆异常
 * @author Looly
 * @author wh
 */
public class CloneRuntimeException extends RuntimeException{
	private static final long serialVersionUID = 6774837422188798989L;

	public CloneRuntimeException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public CloneRuntimeException(String message) {
		super(message);
	}

	public CloneRuntimeException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public CloneRuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public CloneRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
