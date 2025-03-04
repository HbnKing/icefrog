package com.whaleal.icefrog.core.lang;

import com.whaleal.icefrog.core.lang.func.Func0;
import com.whaleal.icefrog.core.util.Predicate;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 简单缓存，无超时实现，默认使用{@link WeakHashMap}实现缓存自动清理
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Looly
 * @author wh
 */
public class SimpleCache<K, V> implements Iterable<Map.Entry<K, V>>, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 池
	 */
	private final Map<K, V> cache;
	// 乐观读写锁
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	/**
	 * 写的时候每个key一把锁，降低锁的粒度
	 */
	protected final Map<K, Lock> keyLockMap = new ConcurrentHashMap<>();

	/**
	 * 构造，默认使用{@link WeakHashMap}实现缓存自动清理
	 */
	public SimpleCache() {
		this(new WeakHashMap<>());
	}

	/**
	 * 构造
	 * <p>
	 * 通过自定义Map初始化，可以自定义缓存实现。<br>
	 * 比如使用{@link WeakHashMap}则会自动清理key，使用HashMap则不会清理<br>
	 * 同时，传入的Map对象也可以自带初始化的键值对，防止在get时创建
	 * </p>
	 *
	 * @param initMap 初始Map，用于定义Map类型
	 */
	public SimpleCache(Map<K, V> initMap) {
		this.cache = initMap;
	}

	/**
	 * 从缓存池中查找值
	 *
	 * @param key 键
	 * @return 值
	 */
	public V get(K key) {
		lock.readLock().lock();
		try {
			return cache.get(key);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * 从缓存中获得对象，当对象不在缓存中或已经过期返回Func0回调产生的对象
	 *
	 * @param key      键
	 * @param supplier 如果不存在回调方法，用于生产值对象
	 * @return 值对象
	 */
	public V get(K key, Func0<V> supplier) {
		return get(key, null, supplier);
	}

	/**
	 * 从缓存中获得对象，当对象不在缓存中或已经过期返回Func0回调产生的对象
	 *
	 * @param key            键
	 * @param validPredicate 检查结果对象是否可用，如是否断开连接等
	 * @param supplier       如果不存在回调方法或结果不可用，用于生产值对象
	 * @return 值对象
	 * @since 1.0.0
	 */
	public V get(K key, Predicate<V> validPredicate, Func0<V> supplier) {
		V v = get(key);
		if (null == v && null != supplier) {
			//每个key单独获取一把锁，降低锁的粒度提高并发能力，see pr#1385@Github
			final Lock keyLock = keyLockMap.computeIfAbsent(key, k -> new ReentrantLock());
			keyLock.lock();
			try {
				// 双重检查，防止在竞争锁的过程中已经有其它线程写入
				v = cache.get(key);
				if (null == v || (null != validPredicate && false == validPredicate.test(v))) {
					try {
						v = supplier.apply();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					put(key, v);
				}
			} finally {
				keyLock.unlock();
				keyLockMap.remove(key);
			}
		}

		return v;
	}

	/**
	 * 放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return 值
	 */
	public V put(K key, V value) {
		// 独占写锁
		lock.writeLock().lock();
		try {
			cache.put(key, value);
		} finally {
			lock.writeLock().unlock();
		}
		return value;
	}

	/**
	 * 移除缓存
	 *
	 * @param key 键
	 * @return 移除的值
	 */
	public V remove(K key) {
		// 独占写锁
		lock.writeLock().lock();
		try {
			return cache.remove(key);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * 清空缓存池
	 */
	public void clear() {
		// 独占写锁
		lock.writeLock().lock();
		try {
			this.cache.clear();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public Iterator<Map.Entry<K, V>> iterator() {
		return this.cache.entrySet().iterator();
	}
}
