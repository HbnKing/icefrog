package com.whaleal.icefrog.core.lang;

/**
 * 匹配接口
 *
 * @param <T> 匹配的对象类型
 * @author Looly
 * @author wh
 */
@FunctionalInterface
public interface Matcher<T> {
	/**
	 * 给定对象是否匹配
	 *
	 * @param t 对象
	 * @return 是否匹配
	 */
	boolean match(T t);
}
