package com.whaleal.icefrog.core.map;

import com.whaleal.icefrog.core.util.StrUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 驼峰Key风格的LinkedHashMap<br>
 * 对KEY转换为驼峰，get("int_value")和get("intValue")获得的值相同，put进入的值也会被覆盖
 *
 * @author Looly
 * @author wh
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @since 1.0.0
 */
public class CamelCaseLinkedMap<K, V> extends CustomKeyMap<K, V> {
	private static final long serialVersionUID = 4043263744224569870L;

	// ------------------------------------------------------------------------- Constructor start
	/**
	 * 构造
	 */
	public CamelCaseLinkedMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity 初始大小
	 */
	public CamelCaseLinkedMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * 构造
	 *
	 * @param m Map
	 */
	public CamelCaseLinkedMap(Map<? extends K, ? extends V> m) {
		this(DEFAULT_LOAD_FACTOR, m);
	}

	/**
	 * 构造
	 *
	 * @param loadFactor 加载因子
	 * @param m Map
	 */
	public CamelCaseLinkedMap(float loadFactor, Map<? extends K, ? extends V> m) {
		this(m.size(), loadFactor);
		this.putAll(m);
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity 初始大小
	 * @param loadFactor 加载因子
	 */
	public CamelCaseLinkedMap(int initialCapacity, float loadFactor) {
		super(new LinkedHashMap<>(initialCapacity, loadFactor));
	}
	// ------------------------------------------------------------------------- Constructor end

	/**
	 * 将Key转为驼峰风格，如果key为字符串的话
	 *
	 * @param key KEY
	 * @return 驼峰Key
	 */
	@Override
	protected Object customKey(Object key) {
		if (key instanceof CharSequence) {
			key = StrUtil.toCamelCase(key.toString());
		}
		return key;
	}
}
