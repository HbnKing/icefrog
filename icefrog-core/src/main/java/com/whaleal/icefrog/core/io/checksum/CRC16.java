package com.whaleal.icefrog.core.io.checksum;

import com.whaleal.icefrog.core.io.checksum.crc16.CRC16Checksum;
import com.whaleal.icefrog.core.io.checksum.crc16.CRC16IBM;

import java.io.Serializable;
import java.util.zip.Checksum;

/**
 * CRC16 循环冗余校验码（Cyclic Redundancy Check）实现，默认IBM算法
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class CRC16 implements Checksum, Serializable {
	private static final long serialVersionUID = 1L;

	private final CRC16Checksum crc16;

	public CRC16(){
		this(new CRC16IBM());
	}

	/**
	 * 构造
	 *
	 * @param crc16Checksum {@link CRC16Checksum} 实现
	 */
	public CRC16(CRC16Checksum crc16Checksum){
		this.crc16 = crc16Checksum;
	}

	@Override
	public long getValue() {
		return crc16.getValue();
	}

	@Override
	public void reset() {
		crc16.reset();
	}

	@Override
	public void update(byte[] b, int off, int len) {
		crc16.update(b, off, len);
	}

	@Override
	public void update(int b) {
		crc16.update(b);
	}
}
