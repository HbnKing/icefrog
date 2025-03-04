package com.whaleal.icefrog.extra.tokenizer;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 分词异常
 *
 * @author Looly
 * @author wh
 */
public class TokenizerException extends RuntimeException {
	private static final long serialVersionUID = 8074865854534335463L;

	public TokenizerException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public TokenizerException(String message) {
		super(message);
	}

	public TokenizerException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public TokenizerException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public TokenizerException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public TokenizerException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
