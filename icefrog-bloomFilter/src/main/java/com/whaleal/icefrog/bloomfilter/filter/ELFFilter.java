package com.whaleal.icefrog.bloomfilter.filter;

import com.whaleal.icefrog.core.util.HashUtil;

public class ELFFilter extends AbstractFilter {
	private static final long serialVersionUID = 1L;

	public ELFFilter(long maxValue, int machineNumber) {
		super(maxValue, machineNumber);
	}

	public ELFFilter(long maxValue) {
		super(maxValue);
	}

	@Override
	public long hash(String str) {
		return HashUtil.elfHash(str) % size;
	}

}
