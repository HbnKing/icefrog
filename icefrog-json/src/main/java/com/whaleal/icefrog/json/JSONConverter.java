package com.whaleal.icefrog.json;

import com.whaleal.icefrog.core.convert.Convert;
import com.whaleal.icefrog.core.convert.ConvertException;
import com.whaleal.icefrog.core.convert.Converter;
import com.whaleal.icefrog.core.convert.ConverterRegistry;
import com.whaleal.icefrog.core.convert.impl.ArrayConverter;
import com.whaleal.icefrog.core.util.ObjectUtil;
import com.whaleal.icefrog.core.util.ReflectUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.core.util.TypeUtil;
import com.whaleal.icefrog.json.serialize.GlobalSerializeMapping;
import com.whaleal.icefrog.json.serialize.JSONDeserializer;

import java.lang.reflect.Type;
import java.util.List;

/**
 * JSON转换器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class JSONConverter implements Converter<JSON> {

	static {
		// 注册到转换中心
		ConverterRegistry registry = ConverterRegistry.getInstance();
		registry.putCustom(JSON.class, JSONConverter.class);
		registry.putCustom(JSONObject.class, JSONConverter.class);
		registry.putCustom(JSONArray.class, JSONConverter.class);
	}

	/**
	 * JSONArray转数组
	 *
	 * @param jsonArray JSONArray
	 * @param arrayClass 数组元素类型
	 * @return 数组对象
	 */
	protected static Object toArray(JSONArray jsonArray, Class<?> arrayClass) {
		return new ArrayConverter(arrayClass).convert(jsonArray, null);
	}

	/**
	 * 将JSONArray转换为指定类型的对量列表
	 *
	 * @param <T> 元素类型
	 * @param jsonArray JSONArray
	 * @param elementType 对象元素类型
	 * @return 对象列表
	 */
	protected static <T> List<T> toList(JSONArray jsonArray, Class<T> elementType) {
		return Convert.toList(elementType, jsonArray);
	}

	/**
	 * JSON递归转换<br>
	 * 首先尝试JDK类型转换，如果失败尝试JSON转Bean<br>
	 * 如果遇到{@link JSONBeanParser}，则调用其{@link JSONBeanParser#parse(Object)}方法转换。
	 *
	 * @param <T> 转换后的对象类型
	 * @param targetType 目标类型
	 * @param value 值
	 * @param ignoreError 是否忽略转换错误
	 * @return 目标类型的值
	 * @throws ConvertException 转换失败
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T jsonConvert(Type targetType, Object value, boolean ignoreError) throws ConvertException {
		if (JSONUtil.isNull(value)) {
			return null;
		}

		// since 5.7.8，增加自定义Bean反序列化接口
		if(targetType instanceof Class){
			final Class<?> clazz = (Class<?>) targetType;
			if (JSONBeanParser.class.isAssignableFrom(clazz)){
				@SuppressWarnings("rawtypes")
				JSONBeanParser target = (JSONBeanParser) ReflectUtil.newInstanceIfPossible(clazz);
				if(null == target){
					throw new ConvertException("Can not instance [{}]", targetType);
				}
				target.parse(value);
				return (T) target;
			}
		}

		return jsonToBean(targetType, value, ignoreError);
	}

	/**
	 * JSON递归转换<br>
	 * 首先尝试JDK类型转换，如果失败尝试JSON转Bean
	 *
	 * @param <T> 转换后的对象类型
	 * @param targetType 目标类型
	 * @param value 值，JSON格式
	 * @param ignoreError 是否忽略转换错误
	 * @return 目标类型的值
	 * @throws ConvertException 转换失败
	 * @since 1.0.0
	 */
	protected static <T> T jsonToBean(Type targetType, Object value, boolean ignoreError) throws ConvertException {
		if (JSONUtil.isNull(value)) {
			return null;
		}

		if(value instanceof JSON){
			final JSONDeserializer<?> deserializer = GlobalSerializeMapping.getDeserializer(targetType);
			if(null != deserializer) {
				//noinspection unchecked
				return (T) deserializer.deserialize((JSON) value);
			}
		}

		final T targetValue = Convert.convertWithCheck(targetType, value, null, ignoreError);

		if (null == targetValue && false == ignoreError) {
			if (StrUtil.isBlankIfStr(value)) {
				// 对于传入空字符串的情况，如果转换的目标对象是非字符串或非原始类型，转换器会返回false。
				// 此处特殊处理，认为返回null属于正常情况
				return null;
			}

			throw new ConvertException("Can not convert {} to type {}", value, ObjectUtil.defaultIfNull(TypeUtil.getClass(targetType), targetType));
		}

		return targetValue;
	}

	@Override
	public JSON convert(Object value, JSON defaultValue) throws IllegalArgumentException {
		return JSONUtil.parse(value);
	}
}
