package com.whaleal.icefrog.bloomfilter.filter;

import com.whaleal.icefrog.bloomfilter.BloomFilter;
import com.whaleal.icefrog.bloomfilter.bitMap.BitMap;
import com.whaleal.icefrog.bloomfilter.bitMap.IntMap;
import com.whaleal.icefrog.bloomfilter.bitMap.LongMap;

/**
 * 抽象Bloom过滤器
 *
 * @author Looly
 * @author wh
 *
 */
public abstract class AbstractFilter implements BloomFilter {
	private static final long serialVersionUID = 1L;

	private BitMap bm = null;

	protected long size = 0;

	/**
	 * 构造
	 *
	 * @param maxValue 最大值
	 * @param machineNum 机器位数
	 */
	public AbstractFilter(long maxValue, int machineNum) {
		init(maxValue, machineNum);
	}

	/**
	 * 构造32位
	 *
	 * @param maxValue 最大值
	 */
	public AbstractFilter(long maxValue) {
		this(maxValue, BitMap.MACHINE32);
	}

	/**
	 * 初始化
	 *
	 * @param maxValue 最大值
	 * @param machineNum 机器位数
	 */
	public void init(long maxValue, int machineNum) {
		this.size = maxValue;
		switch (machineNum) {
		case BitMap.MACHINE32:
			bm = new IntMap((int) (size / machineNum));
			break;
		case BitMap.MACHINE64:
			bm = new LongMap((int) (size / machineNum));
			break;
		default:
			throw new RuntimeException("Error Machine number!");
		}
	}

	@Override
	public boolean contains(String str) {
		return bm.contains(Math.abs(hash(str)));
	}

	@Override
	public boolean add(String str) {
		final long hash = Math.abs(hash(str));
		if (bm.contains(hash)) {
			return false;
		}

		bm.add(hash);
		return true;
	}

	/**
	 * 自定义Hash方法
	 *
	 * @param str 字符串
	 * @return HashCode
	 */
	public abstract long hash(String str);
}
