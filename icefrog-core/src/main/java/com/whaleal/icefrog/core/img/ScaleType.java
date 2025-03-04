package com.whaleal.icefrog.core.img;

import java.awt.*;

/**
 * 图片缩略算法类型
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public enum ScaleType {

	/**
	 * 默认
	 */
	DEFAULT(Image.SCALE_DEFAULT),
	/**
	 * 快速
	 */
	FAST(Image.SCALE_FAST),
	/**
	 * 平滑
	 */
	SMOOTH(Image.SCALE_SMOOTH),
	/**
	 * 使用 ReplicateScaleFilter 类中包含的图像缩放算法
	 */
	REPLICATE(Image.SCALE_REPLICATE),
	/**
	 * Area Averaging算法
	 */
	AREA_AVERAGING(Image.SCALE_AREA_AVERAGING);

	private final int value;

	/**
	 * 构造
	 *
	 * @param value 缩放方式
	 * @see Image#SCALE_DEFAULT
	 * @see Image#SCALE_FAST
	 * @see Image#SCALE_SMOOTH
	 * @see Image#SCALE_REPLICATE
	 * @see Image#SCALE_AREA_AVERAGING
	 */
	ScaleType(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
