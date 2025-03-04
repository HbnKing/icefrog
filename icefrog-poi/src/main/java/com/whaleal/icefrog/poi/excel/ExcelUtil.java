package com.whaleal.icefrog.poi.excel;

import com.whaleal.icefrog.core.exceptions.DependencyException;
import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.io.IoUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;
import com.whaleal.icefrog.core.util.ReUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.poi.PoiChecker;
import com.whaleal.icefrog.poi.excel.cell.CellLocation;
import com.whaleal.icefrog.poi.excel.sax.ExcelSaxReader;
import com.whaleal.icefrog.poi.excel.sax.ExcelSaxUtil;
import com.whaleal.icefrog.poi.excel.sax.handler.RowHandler;

import java.io.File;
import java.io.InputStream;

/**
 * Excel工具类,不建议直接使用index直接操作sheet，在wps/excel中sheet显示顺序与index无关，还有隐藏sheet
 *
 * @author Looly
 * @author wh
 */
public class ExcelUtil {

	/**
	 * xls的ContentType
	 */
	public static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";

	/**
	 * xlsx的ContentType
	 */
	public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	// ------------------------------------------------------------------------------------ Read by Sax start

	/**
	 * 通过Sax方式读取Excel，同时支持03和07格式
	 *
	 * @param path       Excel文件路径
	 * @param rid        sheet rid，-1表示全部Sheet, 0表示第一个Sheet
	 * @param rowHandler 行处理器
	 * @since 1.0.0
	 */
	public static void readBySax(String path, int rid, RowHandler rowHandler) {
		readBySax(FileUtil.file(path), rid, rowHandler);
	}

	/**
	 * 通过Sax方式读取Excel，同时支持03和07格式
	 *
	 * @param path       Excel文件路径
	 * @param idOrRid    Excel中的sheet id或者rid编号，rid必须加rId前缀，例如rId1，如果为-1处理所有编号的sheet
	 * @param rowHandler 行处理器
	 * @since 1.0.0
	 */
	public static void readBySax(String path, String idOrRid, RowHandler rowHandler) {
		readBySax(FileUtil.file(path), idOrRid, rowHandler);
	}

	/**
	 * 通过Sax方式读取Excel，同时支持03和07格式
	 *
	 * @param file       Excel文件
	 * @param rid        sheet rid，-1表示全部Sheet, 0表示第一个Sheet
	 * @param rowHandler 行处理器
	 * @since 1.0.0
	 */
	public static void readBySax(File file, int rid, RowHandler rowHandler) {
		final ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(file), rowHandler);
		reader.read(file, rid);
	}

	/**
	 * 通过Sax方式读取Excel，同时支持03和07格式
	 *
	 * @param file       Excel文件
	 * @param idOrRidOrSheetName    Excel中的sheet id或rid编号或sheet名称，rid必须加rId前缀，例如rId1，如果为-1处理所有编号的sheet
	 * @param rowHandler 行处理器
	 * @since 1.0.0
	 */
	public static void readBySax(File file, String idOrRidOrSheetName, RowHandler rowHandler) {
		final ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(file), rowHandler);
		reader.read(file, idOrRidOrSheetName);
	}

	/**
	 * 通过Sax方式读取Excel，同时支持03和07格式
	 *
	 * @param in         Excel流
	 * @param rid        sheet rid，-1表示全部Sheet, 0表示第一个Sheet
	 * @param rowHandler 行处理器
	 * @since 1.0.0
	 */
	public static void readBySax(InputStream in, int rid, RowHandler rowHandler) {
		in = IoUtil.toMarkSupportStream(in);
		final ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(in), rowHandler);
		reader.read(in, rid);
	}

	/**
	 * 通过Sax方式读取Excel，同时支持03和07格式
	 *
	 * @param in         Excel流
	 * @param idOrRidOrSheetName    Excel中的sheet id或rid编号或sheet名称，rid必须加rId前缀，例如rId1，如果为-1处理所有编号的sheet
	 * @param rowHandler 行处理器
	 * @since 1.0.0
	 */
	public static void readBySax(InputStream in, String idOrRidOrSheetName, RowHandler rowHandler) {
		in = IoUtil.toMarkSupportStream(in);
		final ExcelSaxReader<?> reader = ExcelSaxUtil.createSaxReader(ExcelFileUtil.isXlsx(in), rowHandler);
		reader.read(in, idOrRidOrSheetName);
	}
	// ------------------------------------------------------------------------------------ Read by Sax end

	// ------------------------------------------------------------------------------------------------ getReader

	/**
	 * 获取Excel读取器，通过调用{@link ExcelReader}的read或readXXX方法读取Excel内容<br>
	 * 默认调用第一个sheet
	 *
	 * @param bookFilePath Excel文件路径，绝对路径或相对于ClassPath路径
	 * @return {@link ExcelReader}
	 * @since 1.0.0
	 */
	public static ExcelReader getReader(String bookFilePath) {
		return getReader(bookFilePath, 0);
	}

	/**
	 * 获取Excel读取器，通过调用{@link ExcelReader}的read或readXXX方法读取Excel内容<br>
	 * 默认调用第一个sheet
	 *
	 * @param bookFile Excel文件
	 * @return {@link ExcelReader}
	 */
	public static ExcelReader getReader(File bookFile) {
		return getReader(bookFile, 0);
	}

	/**
	 * 获取Excel读取器，通过调用{@link ExcelReader}的read或readXXX方法读取Excel内容
	 *
	 * @param bookFilePath Excel文件路径，绝对路径或相对于ClassPath路径
	 * @param sheetIndex   sheet序号，0表示第一个sheet
	 * @return {@link ExcelReader}
	 * @since 1.0.0
	 */
	public static ExcelReader getReader(String bookFilePath, int sheetIndex) {
		try {
			return new ExcelReader(bookFilePath, sheetIndex);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获取Excel读取器，通过调用{@link ExcelReader}的read或readXXX方法读取Excel内容
	 *
	 * @param bookFile   Excel文件
	 * @param sheetIndex sheet序号，0表示第一个sheet
	 * @return {@link ExcelReader}
	 */
	public static ExcelReader getReader(File bookFile, int sheetIndex) {
		try {
			return new ExcelReader(bookFile, sheetIndex);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获取Excel读取器，通过调用{@link ExcelReader}的read或readXXX方法读取Excel内容
	 *
	 * @param bookFile  Excel文件
	 * @param sheetName sheet名，第一个默认是sheet1
	 * @return {@link ExcelReader}
	 */
	public static ExcelReader getReader(File bookFile, String sheetName) {
		try {
			return new ExcelReader(bookFile, sheetName);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获取Excel读取器，通过调用{@link ExcelReader}的read或readXXX方法读取Excel内容<br>
	 * 默认调用第一个sheet，读取结束自动关闭流
	 *
	 * @param bookStream Excel文件的流
	 * @return {@link ExcelReader}
	 */
	public static ExcelReader getReader(InputStream bookStream) {
		return getReader(bookStream, 0);
	}

	/**
	 * 获取Excel读取器，通过调用{@link ExcelReader}的read或readXXX方法读取Excel内容<br>
	 * 读取结束自动关闭流
	 *
	 * @param bookStream Excel文件的流
	 * @param sheetIndex sheet序号，0表示第一个sheet
	 * @return {@link ExcelReader}
	 */
	public static ExcelReader getReader(InputStream bookStream, int sheetIndex) {
		try {
			return new ExcelReader(bookStream, sheetIndex);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获取Excel读取器，通过调用{@link ExcelReader}的read或readXXX方法读取Excel内容<br>
	 * 读取结束自动关闭流
	 *
	 * @param bookStream Excel文件的流
	 * @param sheetName  sheet名，第一个默认是sheet1
	 * @return {@link ExcelReader}
	 */
	public static ExcelReader getReader(InputStream bookStream, String sheetName) {
		try {
			return new ExcelReader(bookStream, sheetName);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	// ------------------------------------------------------------------------------------------------ getWriter

	/**
	 * 获得{@link ExcelWriter}，默认写出到第一个sheet<br>
	 * 不传入写出的Excel文件路径，只能调用ExcelWriter#flush(OutputStream)方法写出到流<br>
	 * 若写出到文件，还需调用{@link ExcelWriter#setDestFile(File)}方法自定义写出的文件，然后调用{@link ExcelWriter#flush()}方法写出到文件
	 *
	 * @return {@link ExcelWriter}
	 * @since 1.0.0
	 */
	public static ExcelWriter getWriter() {
		try {
			return new ExcelWriter();
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获得{@link ExcelWriter}，默认写出到第一个sheet<br>
	 * 不传入写出的Excel文件路径，只能调用ExcelWriter#flush(OutputStream)方法写出到流<br>
	 * 若写出到文件，还需调用{@link ExcelWriter#setDestFile(File)}方法自定义写出的文件，然后调用{@link ExcelWriter#flush()}方法写出到文件
	 *
	 * @param isXlsx 是否为xlsx格式
	 * @return {@link ExcelWriter}
	 * @since 1.0.0
	 */
	public static ExcelWriter getWriter(boolean isXlsx) {
		try {
			return new ExcelWriter(isXlsx);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获得{@link ExcelWriter}，默认写出到第一个sheet
	 *
	 * @param destFilePath 目标文件路径
	 * @return {@link ExcelWriter}
	 */
	public static ExcelWriter getWriter(String destFilePath) {
		try {
			return new ExcelWriter(destFilePath);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获得{@link ExcelWriter}，默认写出到第一个sheet
	 *
	 * @param sheetName Sheet名
	 * @return {@link ExcelWriter}
	 * @since 1.0.0
	 */
	public static ExcelWriter getWriterWithSheet(String sheetName) {
		try {
			return new ExcelWriter((File) null, sheetName);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获得{@link ExcelWriter}，默认写出到第一个sheet，名字为sheet1
	 *
	 * @param destFile 目标文件
	 * @return {@link ExcelWriter}
	 */
	public static ExcelWriter getWriter(File destFile) {
		try {
			return new ExcelWriter(destFile);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获得{@link ExcelWriter}
	 *
	 * @param destFilePath 目标文件路径
	 * @param sheetName    sheet表名
	 * @return {@link ExcelWriter}
	 */
	public static ExcelWriter getWriter(String destFilePath, String sheetName) {
		try {
			return new ExcelWriter(destFilePath, sheetName);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获得{@link ExcelWriter}
	 *
	 * @param destFile  目标文件
	 * @param sheetName sheet表名
	 * @return {@link ExcelWriter}
	 */
	public static ExcelWriter getWriter(File destFile, String sheetName) {
		try {
			return new ExcelWriter(destFile, sheetName);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	// ------------------------------------------------------------------------------------------------ getBigWriter

	/**
	 * 获得{@link BigExcelWriter}，默认写出到第一个sheet<br>
	 * 不传入写出的Excel文件路径，只能调用ExcelWriter#flush(OutputStream)方法写出到流<br>
	 * 若写出到文件，还需调用{@link BigExcelWriter#setDestFile(File)}方法自定义写出的文件，然后调用{@link BigExcelWriter#flush()}方法写出到文件
	 *
	 * @return {@link BigExcelWriter}
	 * @since 1.0.0
	 */
	public static ExcelWriter getBigWriter() {
		try {
			return new BigExcelWriter();
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获得{@link BigExcelWriter}，默认写出到第一个sheet<br>
	 * 不传入写出的Excel文件路径，只能调用ExcelWriter#flush(OutputStream)方法写出到流<br>
	 * 若写出到文件，还需调用{@link BigExcelWriter#setDestFile(File)}方法自定义写出的文件，然后调用{@link BigExcelWriter#flush()}方法写出到文件
	 *
	 * @param rowAccessWindowSize 在内存中的行数
	 * @return {@link BigExcelWriter}
	 * @since 1.0.0
	 */
	public static ExcelWriter getBigWriter(int rowAccessWindowSize) {
		try {
			return new BigExcelWriter(rowAccessWindowSize);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获得{@link BigExcelWriter}，默认写出到第一个sheet
	 *
	 * @param destFilePath 目标文件路径
	 * @return {@link BigExcelWriter}
	 */
	public static BigExcelWriter getBigWriter(String destFilePath) {
		try {
			return new BigExcelWriter(destFilePath);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获得{@link BigExcelWriter}，默认写出到第一个sheet，名字为sheet1
	 *
	 * @param destFile 目标文件
	 * @return {@link BigExcelWriter}
	 */
	public static BigExcelWriter getBigWriter(File destFile) {
		try {
			return new BigExcelWriter(destFile);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获得{@link BigExcelWriter}
	 *
	 * @param destFilePath 目标文件路径
	 * @param sheetName    sheet表名
	 * @return {@link BigExcelWriter}
	 */
	public static BigExcelWriter getBigWriter(String destFilePath, String sheetName) {
		try {
			return new BigExcelWriter(destFilePath, sheetName);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 获得{@link BigExcelWriter}
	 *
	 * @param destFile  目标文件
	 * @param sheetName sheet表名
	 * @return {@link BigExcelWriter}
	 */
	public static BigExcelWriter getBigWriter(File destFile, String sheetName) {
		try {
			return new BigExcelWriter(destFile, sheetName);
		} catch (NoClassDefFoundError e) {
			throw new DependencyException(ObjectUtil.defaultIfNull(e.getCause(), e), PoiChecker.NO_POI_ERROR_MSG);
		}
	}

	/**
	 * 将Sheet列号变为列名
	 *
	 * @param index 列号, 从0开始
	 * @return 0-》A; 1-》B...26-》AA
	 * @since 1.0.0
	 */
	public static String indexToColName(int index) {
		if (index < 0) {
			return null;
		}
		final StringBuilder colName = StrUtil.builder();
		do {
			if (colName.length() > 0) {
				index--;
			}
			int remainder = index % 26;
			colName.append((char) (remainder + 'A'));
			index = (index - remainder) / 26;
		} while (index > 0);
		return colName.reverse().toString();
	}

	/**
	 * 根据表元的列名转换为列号
	 *
	 * @param colName 列名, 从A开始
	 * @return A1-》0; B1-》1...AA1-》26
	 * @since 1.0.0
	 */
	public static int colNameToIndex(String colName) {
		int length = colName.length();
		char c;
		int index = -1;
		for (int i = 0; i < length; i++) {
			c = Character.toUpperCase(colName.charAt(i));
			if (Character.isDigit(c)) {
				break;// 确定指定的char值是否为数字
			}
			index = (index + 1) * 26 + (int) c - 'A';
		}
		return index;
	}

	/**
	 * 将Excel中地址标识符（例如A11，B5）等转换为行列表示<br>
	 * 例如：A11 -》 x:0,y:10，B5-》x:1,y:4
	 *
	 * @param locationRef 单元格地址标识符，例如A11，B5
	 * @return 坐标点，x表示行，从0开始，y表示列，从0开始
	 * @since 1.0.0
	 */
	public static CellLocation toLocation(String locationRef) {
		final int x = colNameToIndex(locationRef);
		final int y = ReUtil.getFirstNumber(locationRef) - 1;
		return new CellLocation(x, y);
	}
}
