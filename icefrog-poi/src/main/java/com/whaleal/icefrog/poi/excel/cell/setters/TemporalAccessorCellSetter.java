package com.whaleal.icefrog.poi.excel.cell.setters;

import com.whaleal.icefrog.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * {@link TemporalAccessor} 值单元格设置器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class TemporalAccessorCellSetter implements CellSetter {

	private final TemporalAccessor value;

	/**
	 * 构造
	 *
	 * @param value 值
	 */
	TemporalAccessorCellSetter(TemporalAccessor value) {
		this.value = value;
	}

	@Override
	public void setValue(Cell cell) {
		if (value instanceof Instant) {
			cell.setCellValue(Date.from((Instant) value));
		} else if (value instanceof LocalDateTime) {
			cell.setCellValue((LocalDateTime) value);
		} else if (value instanceof LocalDate) {
			cell.setCellValue((LocalDate) value);
		}
	}
}
