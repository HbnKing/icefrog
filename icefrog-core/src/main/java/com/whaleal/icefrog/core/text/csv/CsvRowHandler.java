package com.whaleal.icefrog.core.text.csv;

/**
 * CSV的行处理器，实现此接口用于按照行处理数据
 *
 * @author Looly 
 * @author wh
 * @since 1.0.0
 */
@FunctionalInterface
public interface CsvRowHandler {

	/**
	 * 处理行数据
	 *
	 * @param row 行数据
	 */
	void handle(CsvRow row);
}
