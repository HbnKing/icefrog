package com.whaleal.icefrog.core.lang;

import com.whaleal.icefrog.core.convert.Convert;
import com.whaleal.icefrog.core.util.NumberUtil;

import java.lang.reflect.Type;

/**
 * 片段表示，用于表示文本、集合等数据结构的一个区间。
 *
 * @param <T> 数字类型，用于表示位置index
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public interface Segment<T extends Number> {

	/**
	 * 获取起始位置
	 *
	 * @return 起始位置
	 */
	T getStartIndex();

	/**
	 * 获取结束位置
	 *
	 * @return 结束位置
	 */
	T getEndIndex();

	/**
	 * 片段长度，默认计算方法为abs({@link #getEndIndex()} - {@link #getEndIndex()})
	 *
	 * @return 片段长度
	 */
	default T length() {
		final T start = Preconditions.notNull(getStartIndex(), "Start index must be not null!");
		final T end = Preconditions.notNull(getEndIndex(), "End index must be not null!");
		return Convert.convert((Type) start.getClass(), NumberUtil.sub(end, start).abs());
	}
}
