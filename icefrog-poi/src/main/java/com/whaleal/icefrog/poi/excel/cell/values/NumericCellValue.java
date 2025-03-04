package com.whaleal.icefrog.poi.excel.cell.values;

import com.whaleal.icefrog.core.date.DateUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.poi.excel.ExcelDateUtil;
import com.whaleal.icefrog.poi.excel.cell.CellValue;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.NumberToTextConverter;

/**
 * 数字类型单元格值<br>
 * 单元格值可能为Long、Double、Date
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class NumericCellValue implements CellValue<Object> {

	private final Cell cell;

	/**
	 * 构造
	 *
	 * @param cell {@link Cell}
	 */
	public NumericCellValue(Cell cell){
		this.cell = cell;
	}

	@Override
	public Object getValue() {
		final double value = cell.getNumericCellValue();

		final CellStyle style = cell.getCellStyle();
		if (null != style) {
			// 判断是否为日期
			if (ExcelDateUtil.isDateFormat(cell)) {
				// 使用icefrog的DateTime包装
				return DateUtil.date(cell.getDateCellValue());
			}

			final String format = style.getDataFormatString();
			// 普通数字
			if (null != format && format.indexOf(StrUtil.C_DOT) < 0) {
				final long longPart = (long) value;
				if (((double) longPart) == value) {
					// 对于无小数部分的数字类型，转为Long
					return longPart;
				}
			}
		}

		// 某些Excel单元格值为double计算结果，可能导致精度问题，通过转换解决精度问题。
		return Double.parseDouble(NumberToTextConverter.toText(value));
	}
}
