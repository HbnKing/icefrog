package com.whaleal.icefrog.log.dialect.logtube;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.log.AbstractLog;
import com.whaleal.icefrog.log.level.Level;
import io.github.logtube.Logtube;
import io.github.logtube.core.IEventLogger;

/**
 * <a href="https://github.com/logtube/logtube-java">LogTube</a> log.封装<br>
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class LogTubeLog extends AbstractLog {

	private final IEventLogger logger;

	// ------------------------------------------------------------------------- Constructor
	public LogTubeLog(IEventLogger logger) {
		this.logger = logger;
	}

	public LogTubeLog(Class<?> clazz) {
		this((null == clazz) ? StrUtil.NULL : clazz.getName());
	}

	public LogTubeLog(String name) {
		this(Logtube.getLogger(name));
	}


	@Override
	public String getName() {
		return logger.getName();
	}

	// ------------------------------------------------------------------------- Trace
	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void trace(String fqcn, Throwable t, String format, Object... arguments) {
		log(fqcn, Level.TRACE, t, format, arguments);
	}

	// ------------------------------------------------------------------------- Debug
	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(String fqcn, Throwable t, String format, Object... arguments) {
		log(fqcn, Level.DEBUG, t, format, arguments);
	}

	// ------------------------------------------------------------------------- Info
	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void info(String fqcn, Throwable t, String format, Object... arguments) {
		log(fqcn, Level.INFO, t, format, arguments);
	}

	// ------------------------------------------------------------------------- Warn
	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public void warn(String fqcn, Throwable t, String format, Object... arguments) {
		log(fqcn, Level.WARN, t, format, arguments);
	}

	// ------------------------------------------------------------------------- Error
	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void error(String fqcn, Throwable t, String format, Object... arguments) {
		log(fqcn, Level.ERROR, t, format, arguments);
	}

	@Override
	public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
		final String topic = level.name().toLowerCase();
		logger.topic(topic)
				.xStackTraceElement(ExceptionUtil.getStackElement(6), null)
				.message(StrUtil.format(format, arguments))
				.xException(t)
				.commit();
	}
}
