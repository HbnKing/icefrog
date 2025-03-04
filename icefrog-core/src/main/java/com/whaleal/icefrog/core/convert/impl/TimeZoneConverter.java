package com.whaleal.icefrog.core.convert.impl;

import java.util.TimeZone;

import com.whaleal.icefrog.core.convert.AbstractConverter;

/**
 * TimeZone转换器
 * @author Looly
 * @author wh
 *
 */
public class TimeZoneConverter extends AbstractConverter<TimeZone>{
	private static final long serialVersionUID = 1L;

	@Override
	protected TimeZone convertInternal(Object value) {
		return TimeZone.getTimeZone(convertToStr(value));
	}

}
