package com.whaleal.icefrog.extra.cglib;

import com.whaleal.icefrog.core.lang.SimpleCache;
import com.whaleal.icefrog.core.util.StrUtil;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

/**
 * BeanCopier属性缓存<br>
 * 缓存用于防止多次反射造成的性能问题
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public enum BeanCopierCache {
	/**
	 * BeanCopier属性缓存单例
	 */
	INSTANCE;

	private final SimpleCache<String, BeanCopier> cache = new SimpleCache<>();

	/**
	 * 获得类与转换器生成的key在{@link BeanCopier}的Map中对应的元素
	 *
	 * @param srcClass    源Bean的类
	 * @param targetClass 目标Bean的类
	 * @param converter   转换器
	 * @return Map中对应的BeanCopier
	 */
	public BeanCopier get(Class<?> srcClass, Class<?> targetClass, Converter converter) {
		final String key = genKey(srcClass, targetClass, converter);
		return cache.get(key, () -> BeanCopier.create(srcClass, targetClass, converter != null));
	}

	/**
	 * 获得类与转换器生成的key
	 *
	 * @param srcClass    源Bean的类
	 * @param targetClass 目标Bean的类
	 * @param converter   转换器
	 * @return 属性名和Map映射的key
	 */
	private String genKey(Class<?> srcClass, Class<?> targetClass, Converter converter) {
		final StringBuilder key = StrUtil.builder()
				.append(srcClass.getName()).append('#').append(targetClass.getName());
		if(null != converter){
			key.append('#').append(converter.getClass().getName());
		}
		return key.toString();
	}
}
