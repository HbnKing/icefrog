package com.whaleal.icefrog.poi.excel.cell;

import com.whaleal.icefrog.core.util.ObjectUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.poi.excel.ExcelUtil;
import com.whaleal.icefrog.poi.excel.StyleSet;
import com.whaleal.icefrog.poi.excel.cell.setters.CellSetterFactory;
import com.whaleal.icefrog.poi.excel.cell.values.ErrorCellValue;
import com.whaleal.icefrog.poi.excel.cell.values.NumericCellValue;
import com.whaleal.icefrog.poi.excel.editors.TrimEditor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.SheetUtil;

import java.math.BigDecimal;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * Excel表格中单元格工具类
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class CellUtil {

	/**
	 * 获取单元格值
	 *
	 * @param cell {@link Cell}单元格
	 * @return 值，类型可能为：Date、Double、Boolean、String
	 * @since 1.0.0
	 */
	public static Object getCellValue(Cell cell) {
		return getCellValue(cell, false);
	}

	/**
	 * 获取单元格值
	 *
	 * @param cell            {@link Cell}单元格
	 * @param isTrimCellValue 如果单元格类型为字符串，是否去掉两边空白符
	 * @return 值，类型可能为：Date、Double、Boolean、String
	 */
	public static Object getCellValue(Cell cell, boolean isTrimCellValue) {
		if (null == cell) {
			return null;
		}
		return getCellValue(cell, cell.getCellType(), isTrimCellValue);
	}

	/**
	 * 获取单元格值
	 *
	 * @param cell       {@link Cell}单元格
	 * @param cellEditor 单元格值编辑器。可以通过此编辑器对单元格值做自定义操作
	 * @return 值，类型可能为：Date、Double、Boolean、String
	 */
	public static Object getCellValue(Cell cell, CellEditor cellEditor) {
		return getCellValue(cell, null, cellEditor);
	}

	/**
	 * 获取单元格值
	 *
	 * @param cell            {@link Cell}单元格
	 * @param cellType        单元格值类型{@link CellType}枚举
	 * @param isTrimCellValue 如果单元格类型为字符串，是否去掉两边空白符
	 * @return 值，类型可能为：Date、Double、Boolean、String
	 */
	public static Object getCellValue(Cell cell, CellType cellType, final boolean isTrimCellValue) {
		return getCellValue(cell, cellType, isTrimCellValue ? new TrimEditor() : null);
	}

	/**
	 * 获取单元格值<br>
	 * 如果单元格值为数字格式，则判断其格式中是否有小数部分，无则返回Long类型，否则返回Double类型
	 *
	 * @param cell       {@link Cell}单元格
	 * @param cellType   单元格值类型{@link CellType}枚举，如果为{@code null}默认使用cell的类型
	 * @param cellEditor 单元格值编辑器。可以通过此编辑器对单元格值做自定义操作
	 * @return 值，类型可能为：Date、Double、Boolean、String
	 */
	public static Object getCellValue(Cell cell, CellType cellType, CellEditor cellEditor) {
		if (null == cell) {
			return null;
		}
		if (cell instanceof NullCell) {
			return null == cellEditor ? null : cellEditor.edit(cell, null);
		}
		if (null == cellType) {
			cellType = cell.getCellType();
		}

		// 尝试获取合并单元格，如果是合并单元格，则重新获取单元格类型
		final Cell mergedCell = getMergedRegionCell(cell);
		if (mergedCell != cell) {
			cell = mergedCell;
			cellType = cell.getCellType();
		}

		Object value;
		switch (cellType) {
			case NUMERIC:
				value = new NumericCellValue(cell).getValue();
				break;
			case BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case FORMULA:
				value = getCellValue(cell, cell.getCachedFormulaResultType(), cellEditor);
				break;
			case BLANK:
				value = StrUtil.EMPTY;
				break;
			case ERROR:
				value = new ErrorCellValue(cell).getValue();
				break;
			default:
				value = cell.getStringCellValue();
		}

		return null == cellEditor ? value : cellEditor.edit(cell, value);
	}

	/**
	 * 设置单元格值<br>
	 * 根据传入的styleSet自动匹配样式<br>
	 * 当为头部样式时默认赋值头部样式，但是头部中如果有数字、日期等类型，将按照数字、日期样式设置
	 *
	 * @param cell     单元格
	 * @param value    值
	 * @param styleSet 单元格样式集，包括日期等样式，null表示无样式
	 * @param isHeader 是否为标题单元格
	 */
	public static void setCellValue(Cell cell, Object value, StyleSet styleSet, boolean isHeader) {
		if (null == cell) {
			return;
		}

		if (null != styleSet) {
			final CellStyle headCellStyle = styleSet.getHeadCellStyle();
			final CellStyle cellStyle = styleSet.getCellStyle();
			if (isHeader && null != headCellStyle) {
				cell.setCellStyle(headCellStyle);
			} else if (null != cellStyle) {
				cell.setCellStyle(cellStyle);
			}
		}

		if (value instanceof Date
				|| value instanceof TemporalAccessor
				|| value instanceof Calendar) {
			// 日期单独定义格式
			if (null != styleSet && null != styleSet.getCellStyleForDate()) {
				cell.setCellStyle(styleSet.getCellStyleForDate());
			}
		} else if (value instanceof Number) {
			// 数字单独定义格式
			if ((value instanceof Double || value instanceof Float || value instanceof BigDecimal) && null != styleSet && null != styleSet.getCellStyleForNumber()) {
				cell.setCellStyle(styleSet.getCellStyleForNumber());
			}
		}

		setCellValue(cell, value);
	}

	/**
	 * 设置单元格值<br>
	 * 根据传入的styleSet自动匹配样式<br>
	 * 当为头部样式时默认赋值头部样式，但是头部中如果有数字、日期等类型，将按照数字、日期样式设置
	 *
	 * @param cell  单元格
	 * @param value 值
	 * @param style 自定义样式，null表示无样式
	 */
	public static void setCellValue(Cell cell, Object value, CellStyle style) {
		setCellValue(cell, (CellSetter) cell1 -> {
			if (null != style) {
				cell1.setCellStyle(style);
				setCellValue(cell, value);
			}
		});
	}

	/**
	 * 设置单元格值<br>
	 * 根据传入的styleSet自动匹配样式<br>
	 * 当为头部样式时默认赋值头部样式，但是头部中如果有数字、日期等类型，将按照数字、日期样式设置
	 *
	 * @param cell  单元格
	 * @param value 值
	 * @since 1.0.0
	 */
	public static void setCellValue(Cell cell, Object value) {
		if (null == cell) {
			return;
		}

		// issue#1659@Github
		// 在使用BigWriter(SXSSF)模式写出数据时，单元格值为直接值，非引用值（is标签）
		// 而再使用ExcelWriter(XSSF)编辑时，会写出引用值，导致失效。
		// 此处做法是先清空单元格值，再写入
		if(CellType.BLANK != cell.getCellType()){
			cell.setBlank();
		}

		CellSetterFactory.createCellSetter(value).setValue(cell);
	}

	/**
	 * 获取单元格，如果单元格不存在，返回{@link NullCell}
	 *
	 * @param row       Excel表的行
	 * @param cellIndex 列号
	 * @return {@link Row}
	 * @since 1.0.0
	 */
	public static Cell getCell(Row row, int cellIndex) {
		Cell cell = row.getCell(cellIndex);
		if (null == cell) {
			return new NullCell(row, cellIndex);
		}
		return cell;
	}

	/**
	 * 获取已有单元格或创建新单元格
	 *
	 * @param row       Excel表的行
	 * @param cellIndex 列号
	 * @return {@link Row}
	 * @since 1.0.0
	 */
	public static Cell getOrCreateCell(Row row, int cellIndex) {
		Cell cell = row.getCell(cellIndex);
		if (null == cell) {
			cell = row.createCell(cellIndex);
		}
		return cell;
	}

	/**
	 * 判断指定的单元格是否是合并单元格
	 *
	 * @param sheet       {@link Sheet}
	 * @param locationRef 单元格地址标识符，例如A11，B5
	 * @return 是否是合并单元格
	 * @since 1.0.0
	 */
	public static boolean isMergedRegion(Sheet sheet, String locationRef) {
		final CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
		return isMergedRegion(sheet, cellLocation.getX(), cellLocation.getY());
	}

	/**
	 * 判断指定的单元格是否是合并单元格
	 *
	 * @param cell {@link Cell}
	 * @return 是否是合并单元格
	 * @since 1.0.0
	 */
	public static boolean isMergedRegion(Cell cell) {
		return isMergedRegion(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex());
	}

	/**
	 * 判断指定的单元格是否是合并单元格
	 *
	 * @param sheet {@link Sheet}
	 * @param x     列号，从0开始
	 * @param y     行号，从0开始
	 * @return 是否是合并单元格
	 */
	public static boolean isMergedRegion(Sheet sheet, int x, int y) {
		final int sheetMergeCount = sheet.getNumMergedRegions();
		CellRangeAddress ca;
		for (int i = 0; i < sheetMergeCount; i++) {
			ca = sheet.getMergedRegion(i);
			if (y >= ca.getFirstRow() && y <= ca.getLastRow()
					&& x >= ca.getFirstColumn() && x <= ca.getLastColumn()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 合并单元格，可以根据设置的值来合并行和列
	 *
	 * @param sheet       表对象
	 * @param firstRow    起始行，0开始
	 * @param lastRow     结束行，0开始
	 * @param firstColumn 起始列，0开始
	 * @param lastColumn  结束列，0开始
	 * @return 合并后的单元格号
	 */
	public static int mergingCells(Sheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn) {
		return mergingCells(sheet, firstRow, lastRow, firstColumn, lastColumn, null);
	}

	/**
	 * 合并单元格，可以根据设置的值来合并行和列
	 *
	 * @param sheet       表对象
	 * @param firstRow    起始行，0开始
	 * @param lastRow     结束行，0开始
	 * @param firstColumn 起始列，0开始
	 * @param lastColumn  结束列，0开始
	 * @param cellStyle   单元格样式，只提取边框样式，null表示无样式
	 * @return 合并后的单元格号
	 */
	public static int mergingCells(Sheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn, CellStyle cellStyle) {
		final CellRangeAddress cellRangeAddress = new CellRangeAddress(//
				firstRow, // first row (0-based)
				lastRow, // last row (0-based)
				firstColumn, // first column (0-based)
				lastColumn // last column (0-based)
		);

		if (null != cellStyle) {
			RegionUtil.setBorderTop(cellStyle.getBorderTop(), cellRangeAddress, sheet);
			RegionUtil.setBorderRight(cellStyle.getBorderRight(), cellRangeAddress, sheet);
			RegionUtil.setBorderBottom(cellStyle.getBorderBottom(), cellRangeAddress, sheet);
			RegionUtil.setBorderLeft(cellStyle.getBorderLeft(), cellRangeAddress, sheet);
			RegionUtil.setTopBorderColor(cellStyle.getTopBorderColor(),cellRangeAddress,sheet);
			RegionUtil.setRightBorderColor(cellStyle.getRightBorderColor(),cellRangeAddress,sheet);
			RegionUtil.setLeftBorderColor(cellStyle.getLeftBorderColor(),cellRangeAddress,sheet);
			RegionUtil.setBottomBorderColor(cellStyle.getBottomBorderColor(),cellRangeAddress,sheet);
		}
		return sheet.addMergedRegion(cellRangeAddress);
	}

	/**
	 * 获取合并单元格的值<br>
	 * 传入的x,y坐标（列行数）可以是合并单元格范围内的任意一个单元格
	 *
	 * @param sheet       {@link Sheet}
	 * @param locationRef 单元格地址标识符，例如A11，B5
	 * @return 合并单元格的值
	 * @since 1.0.0
	 */
	public static Object getMergedRegionValue(Sheet sheet, String locationRef) {
		final CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
		return getMergedRegionValue(sheet, cellLocation.getX(), cellLocation.getY());
	}

	/**
	 * 获取合并单元格的值<br>
	 * 传入的x,y坐标（列行数）可以是合并单元格范围内的任意一个单元格
	 *
	 * @param sheet {@link Sheet}
	 * @param x     列号，从0开始，可以是合并单元格范围中的任意一列
	 * @param y     行号，从0开始，可以是合并单元格范围中的任意一行
	 * @return 合并单元格的值
	 * @since 1.0.0
	 */
	public static Object getMergedRegionValue(Sheet sheet, int x, int y) {
		// 合并单元格的识别在getCellValue已经集成，无需重复获取合并单元格
		return getCellValue(SheetUtil.getCell(sheet, x, y));
	}

	/**
	 * 获取合并单元格<br>
	 * 传入的x,y坐标（列行数）可以是合并单元格范围内的任意一个单元格
	 *
	 * @param cell {@link Cell}
	 * @return 合并单元格
	 * @since 1.0.0
	 */
	public static Cell getMergedRegionCell(Cell cell) {
		if (null == cell) {
			return null;
		}
		return ObjectUtil.defaultIfNull(
				getCellIfMergedRegion(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex()),
				cell);
	}

	/**
	 * 获取合并单元格<br>
	 * 传入的x,y坐标（列行数）可以是合并单元格范围内的任意一个单元格
	 *
	 * @param sheet {@link Sheet}
	 * @param x     列号，从0开始，可以是合并单元格范围中的任意一列
	 * @param y     行号，从0开始，可以是合并单元格范围中的任意一行
	 * @return 合并单元格，如果非合并单元格，返回坐标对应的单元格
	 * @since 1.0.0
	 */
	public static Cell getMergedRegionCell(Sheet sheet, int x, int y) {
		return ObjectUtil.defaultIfNull(
				getCellIfMergedRegion(sheet, x, y),
				SheetUtil.getCell(sheet, y, x));
	}

	/**
	 * 为特定单元格添加批注
	 *
	 * @param cell          单元格
	 * @param commentText   批注内容
	 * @param commentAuthor 作者
	 * @param anchor        批注的位置、大小等信息，null表示使用默认
	 * @since 1.0.0
	 */
	public static void setComment(Cell cell, String commentText, String commentAuthor, ClientAnchor anchor) {
		final Sheet sheet = cell.getSheet();
		final Workbook wb = sheet.getWorkbook();
		final Drawing<?> drawing = sheet.createDrawingPatriarch();
		final CreationHelper factory = wb.getCreationHelper();
		if (anchor == null) {
			anchor = factory.createClientAnchor();
			anchor.setCol1(cell.getColumnIndex() + 1);
			anchor.setCol2(cell.getColumnIndex() + 3);
			anchor.setRow1(cell.getRowIndex());
			anchor.setRow2(cell.getRowIndex() + 2);
		}
		Comment comment = drawing.createCellComment(anchor);
		comment.setString(factory.createRichTextString(commentText));
		comment.setAuthor(StrUtil.nullToEmpty(commentText));
		cell.setCellComment(comment);
	}

	// -------------------------------------------------------------------------------------------------------------- Private method start

	/**
	 * 获取合并单元格，非合并单元格返回{@code null}<br>
	 * 传入的x,y坐标（列行数）可以是合并单元格范围内的任意一个单元格
	 *
	 * @param sheet {@link Sheet}
	 * @param x     列号，从0开始，可以是合并单元格范围中的任意一列
	 * @param y     行号，从0开始，可以是合并单元格范围中的任意一行
	 * @return 合并单元格，如果非合并单元格，返回{@code null}
	 * @since 1.0.0
	 */
	private static Cell getCellIfMergedRegion(Sheet sheet, int x, int y) {
		final int sheetMergeCount = sheet.getNumMergedRegions();
		CellRangeAddress ca;
		for (int i = 0; i < sheetMergeCount; i++) {
			ca = sheet.getMergedRegion(i);
			if (ca.isInRange(y, x)) {
				return SheetUtil.getCell(sheet, ca.getFirstRow(), ca.getFirstColumn());
			}
		}
		return null;
	}
	// -------------------------------------------------------------------------------------------------------------- Private method end
}
