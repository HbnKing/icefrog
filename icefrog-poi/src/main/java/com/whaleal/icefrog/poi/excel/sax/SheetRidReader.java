package com.whaleal.icefrog.poi.excel.sax;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.io.IORuntimeException;
import com.whaleal.icefrog.core.io.IoUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.poi.exceptions.POIException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 在Sax方式读取Excel时，读取sheet标签中sheetId和rid的对应关系，类似于:
 * <pre>
 * &lt;sheet name="Sheet6" sheetId="4" r:id="rId6"/&gt;
 * </pre>
 * <p>
 * 读取结果为：
 *
 * <pre>
 *     {"4": "6"}
 * </pre>
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class SheetRidReader extends DefaultHandler {

	private final static String TAG_NAME = "sheet";
	private final static String RID_ATTR = "r:id";
	private final static String SHEET_ID_ATTR = "sheetId";
	private final static String NAME_ATTR = "name";

	private final Map<Integer, Integer> ID_RID_MAP = new LinkedHashMap<>();
	private final Map<String, Integer> NAME_RID_MAP = new LinkedHashMap<>();

	/**
	 * 读取Wordkbook的XML中sheet标签中sheetId和rid的对应关系
	 *
	 * @param xssfReader XSSF读取器
	 * @return this
	 */
	public SheetRidReader read(XSSFReader xssfReader) {
		InputStream workbookData = null;
		try {
			workbookData = xssfReader.getWorkbookData();
			ExcelSaxUtil.readFrom(workbookData, this);
		} catch (InvalidFormatException e) {
			throw new POIException(e);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			IoUtil.close(workbookData);
		}
		return this;
	}

	/**
	 * 根据sheetId获取rid，从1开始
	 *
	 * @param sheetId Sheet的ID，从1开始
	 * @return rid，从1开始
	 */
	public Integer getRidBySheetId(int sheetId) {
		return ID_RID_MAP.get(sheetId);
	}

	/**
	 * 根据sheetId获取rid，从0开始
	 *
	 * @param sheetId Sheet的ID，从0开始
	 * @return rid，从0开始
	 * @since 1.0.0
	 */
	public Integer getRidBySheetIdBase0(int sheetId) {
		final Integer rid = getRidBySheetId(sheetId + 1);
		if (null != rid) {
			return rid - 1;
		}
		return null;
	}

	/**
	 * 根据sheet name获取rid，从1开始
	 *
	 * @param sheetName Sheet的name
	 * @return rid，从1开始
	 */
	public Integer getRidByName(String sheetName) {
		return NAME_RID_MAP.get(sheetName);
	}

	/**
	 * 根据sheet name获取rid，从0开始
	 *
	 * @param sheetName Sheet的name
	 * @return rid，从0开始
	 * @since 1.0.0
	 */
	public Integer getRidByNameBase0(String sheetName) {
		final Integer rid = getRidByName(sheetName);
		if (null != rid) {
			return rid - 1;
		}
		return null;
	}

	/**
	 * 通过sheet的序号获取rid
	 *
	 * @param index 序号，从0开始
	 * @return rid
	 * @since 1.0.0
	 */
	public Integer getRidByIndex(int index) {
		return CollUtil.get(this.NAME_RID_MAP.values(), index);
	}

	/**
	 * 通过sheet的序号获取rid
	 *
	 * @param index 序号，从0开始
	 * @return rid，从0开始
	 * @since 1.0.0
	 */
	public Integer getRidByIndexBase0(int index) {
		final Integer rid = CollUtil.get(this.NAME_RID_MAP.values(), index);
		if (null != rid) {
			return rid - 1;
		}
		return null;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (TAG_NAME.equalsIgnoreCase(localName)) {
			final String ridStr = attributes.getValue(RID_ATTR);
			if (StrUtil.isEmpty(ridStr)) {
				return;
			}
			final int rid = Integer.parseInt(StrUtil.removePrefixIgnoreCase(ridStr, Excel07SaxReader.RID_PREFIX));

			// sheet名和rid映射
			final String name = attributes.getValue(NAME_ATTR);
			if (StrUtil.isNotEmpty(name)) {
				NAME_RID_MAP.put(name, rid);
			}

			// sheetId和rid映射
			final String sheetIdStr = attributes.getValue(SHEET_ID_ATTR);
			if (StrUtil.isNotEmpty(sheetIdStr)) {
				ID_RID_MAP.put(Integer.parseInt(sheetIdStr), rid);
			}
		}
	}
}
