package com.whaleal.icefrog.core.convert.impl;

import com.whaleal.icefrog.core.bean.BeanUtil;
import com.whaleal.icefrog.core.convert.AbstractConverter;
import com.whaleal.icefrog.core.convert.ConverterRegistry;
import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.core.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * {@link Map} 转换器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class MapConverter extends AbstractConverter<Map<?, ?>> {
	private static final long serialVersionUID = 1L;

	/** Map类型 */
	private final Type mapType;
	/** 键类型 */
	private final Type keyType;
	/** 值类型 */
	private final Type valueType;

	/**
	 * 构造，Map的key和value泛型类型自动获取
	 *
	 * @param mapType Map类型
	 */
	public MapConverter(Type mapType) {
		this(mapType, TypeUtil.getTypeArgument(mapType, 0), TypeUtil.getTypeArgument(mapType, 1));
	}

	/**
	 * 构造
	 *
	 * @param mapType Map类型
	 * @param keyType 键类型
	 * @param valueType 值类型
	 */
	public MapConverter(Type mapType, Type keyType, Type valueType) {
		this.mapType = mapType;
		this.keyType = keyType;
		this.valueType = valueType;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map<?, ?> convertInternal(Object value) {
		Map map;
		if (value instanceof Map) {
			final Type[] typeArguments = TypeUtil.getTypeArguments(value.getClass());
			if (null != typeArguments //
					&& 2 == typeArguments.length//
					&& Objects.equals(this.keyType, typeArguments[0]) //
					&& Objects.equals(this.valueType, typeArguments[1])) {
				//对于键值对类型一致的Map对象，不再做转换，直接返回原对象
				return (Map) value;
			}
			map = MapUtil.createMap(TypeUtil.getClass(this.mapType));
			convertMapToMap((Map) value, map);
		} else if (BeanUtil.isBean(value.getClass())) {
			map = BeanUtil.beanToMap(value);
			// 二次转换，转换键值类型
			map = convertInternal(map);
		} else {
			throw new UnsupportedOperationException(StrUtil.format("Unsupport toMap value type: {}", value.getClass().getName()));
		}
		return map;
	}

	/**
	 * Map转Map
	 *
	 * @param srcMap 源Map
	 * @param targetMap 目标Map
	 */
	private void convertMapToMap(Map<?, ?> srcMap, Map<Object, Object> targetMap) {
		final ConverterRegistry convert = ConverterRegistry.getInstance();
		Object key;
		Object value;
		for (Entry<?, ?> entry : srcMap.entrySet()) {
			key = TypeUtil.isUnknown(this.keyType) ? entry.getKey() : convert.convert(this.keyType, entry.getKey());
			value = TypeUtil.isUnknown(this.valueType) ? entry.getValue() : convert.convert(this.valueType, entry.getValue());
			targetMap.put(key, value);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Map<?, ?>> getTargetType() {
		return (Class<Map<?, ?>>) TypeUtil.getClass(this.mapType);
	}
}
