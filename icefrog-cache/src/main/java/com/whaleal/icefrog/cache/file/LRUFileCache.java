package com.whaleal.icefrog.cache.file;

import java.io.File;

import com.whaleal.icefrog.cache.Cache;
import com.whaleal.icefrog.cache.impl.LRUCache;

/**
 *  使用LRU缓存文件，以解决频繁读取文件引起的性能问题
 * @author Looly
 * @author wh
 *
 */
public class LRUFileCache extends AbstractFileCache{
	private static final long serialVersionUID = 1L;

	/**
	 * 构造<br>
	 * 最大文件大小为缓存容量的一半<br>
	 * 默认无超时
	 * @param capacity 缓存容量
	 */
	public LRUFileCache(int capacity) {
		this(capacity, capacity / 2, 0);
	}

	/**
	 * 构造<br>
	 * 默认无超时
	 * @param capacity 缓存容量
	 * @param maxFileSize 最大文件大小
	 */
	public LRUFileCache(int capacity, int maxFileSize) {
		this(capacity, maxFileSize, 0);
	}

	/**
	 * 构造
	 * @param capacity 缓存容量
	 * @param maxFileSize 文件最大大小
	 * @param timeout 默认超时时间，0表示无默认超时
	 */
	public LRUFileCache(int capacity, int maxFileSize, long timeout) {
		super(capacity, maxFileSize, timeout);
	}

	@Override
	protected Cache<File, byte[]> initCache() {
		return new LRUCache<File, byte[]>(LRUFileCache.this.capacity, super.timeout) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isFull() {
				return LRUFileCache.this.usedSize > this.capacity;
			}

			@Override
			protected void onRemove(File key, byte[] cachedObject) {
				usedSize -= cachedObject.length;
			}
		};
	}

}
