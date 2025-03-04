package com.whaleal.icefrog.poi.excel.sax;

import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.poi.exceptions.POIException;

import java.io.File;
import java.io.InputStream;

/**
 * Sax方式读取Excel接口，提供一些共用方法
 * @author Looly
 * @author wh
 *
 * @param <T> 子对象类型，用于标记返回值this
 * @since 1.0.0
 */
public interface ExcelSaxReader<T> {

	// sheet r:Id前缀
	String RID_PREFIX = "rId";
	// sheet name前缀
	String SHEET_NAME_PREFIX = "sheetName:";

	/**
	 * 开始读取Excel
	 *
	 * @param file Excel文件
	 * @param idOrRidOrSheetName Excel中的sheet id或者rid编号或sheet名称，rid必须加rId前缀，例如rId1，如果为-1处理所有编号的sheet
	 * @return this
	 * @throws POIException POI异常
	 */
	T read(File file, String idOrRidOrSheetName) throws POIException;

	/**
	 * 开始读取Excel，读取结束后并不关闭流
	 *
	 * @param in Excel流
	 * @param idOrRidOrSheetName Excel中的sheet id或者rid编号，rid必须加rId前缀，例如rId1，如果为-1处理所有编号的sheet
	 * @return this
	 * @throws POIException POI异常
	 */
	T read(InputStream in, String idOrRidOrSheetName) throws POIException;

	/**
	 * 开始读取Excel，读取所有sheet
	 *
	 * @param path Excel文件路径
	 * @return this
	 * @throws POIException POI异常
	 */
	default T read(String path) throws POIException {
		return read(FileUtil.file(path));
	}

	/**
	 * 开始读取Excel，读取所有sheet
	 *
	 * @param file Excel文件
	 * @return this
	 * @throws POIException POI异常
	 */
	default T read(File file) throws POIException {
		return read(file, -1);
	}

	/**
	 * 开始读取Excel，读取所有sheet，读取结束后并不关闭流
	 *
	 * @param in Excel包流
	 * @return this
	 * @throws POIException POI异常
	 */
	default T read(InputStream in) throws POIException {
		return read(in, -1);
	}

	/**
	 * 开始读取Excel
	 *
	 * @param path 文件路径
	 * @param idOrRidOrSheetName Excel中的sheet id或者rid编号或sheet名称，rid必须加rId前缀，例如rId1，如果为-1处理所有编号的sheet
	 * @return this
	 * @throws POIException POI异常
	 */
	default T read(String path, int idOrRidOrSheetName) throws POIException {
		return read(FileUtil.file(path), idOrRidOrSheetName);
	}

	/**
	 * 开始读取Excel
	 *
	 * @param path 文件路径
	 * @param idOrRidOrSheetName Excel中的sheet id或者rid编号或sheet名称，rid必须加rId前缀，例如rId1，如果为-1处理所有编号的sheet
	 * @return this
	 * @throws POIException POI异常
	 */
	default T read(String path, String idOrRidOrSheetName) throws POIException {
		return read(FileUtil.file(path), idOrRidOrSheetName);
	}

	/**
	 * 开始读取Excel
	 *
	 * @param file Excel文件
	 * @param rid Excel中的sheet rid编号，如果为-1处理所有编号的sheet
	 * @return this
	 * @throws POIException POI异常
	 */
	default T read(File file, int rid) throws POIException{
		return read(file, String.valueOf(rid));
	}

	/**
	 * 开始读取Excel，读取结束后并不关闭流
	 *
	 * @param in Excel流
	 * @param rid Excel中的sheet rid编号，如果为-1处理所有编号的sheet
	 * @return this
	 * @throws POIException POI异常
	 */
	default T read(InputStream in, int rid) throws POIException{
		return read(in, String.valueOf(rid));
	}
}
