package com.whaleal.icefrog.aop.aspects;

import com.whaleal.icefrog.core.date.TimeInterval;
import com.whaleal.icefrog.core.lang.Console;

import java.lang.reflect.Method;

/**
 * 通过日志打印方法的执行时间的切面
 *
 * @author Looly
 * @author wh
 */
public class TimeIntervalAspect extends SimpleAspect {
	private static final long serialVersionUID = 1L;

	private final TimeInterval interval = new TimeInterval();

	@Override
	public boolean before(Object target, Method method, Object[] args) {
		interval.start();
		return true;
	}

	@Override
	public boolean after(Object target, Method method, Object[] args, Object returnVal) {
		Console.log("Method [{}.{}] execute spend [{}]ms return value [{}]",
				target.getClass().getName(), //
				method.getName(), //
				interval.intervalMs(), //
				returnVal);
		return true;
	}
}
