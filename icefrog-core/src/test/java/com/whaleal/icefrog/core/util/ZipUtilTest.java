package com.whaleal.icefrog.core.util;

import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.io.IORuntimeException;
import com.whaleal.icefrog.core.lang.Console;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * {@link ZipUtil}单元测试
 * @author Looly
 * @author wh
 *
 */
public class ZipUtilTest {


	@Test
	@Ignore
	public void zipDirTest() {
		ZipUtil.zip(new File("d:/test"));
	}

	@Test
	@Ignore
	public void unzipTest() {
		File unzip = ZipUtil.unzip("f:/test/apache-maven-3.6.2.zip", "f:\\test");
		Console.log(unzip);
	}

	@Test
	@Ignore
	public void unzipTest2() {
		File unzip = ZipUtil.unzip("f:/test/各种资源.zip", "f:/test/各种资源", CharsetUtil.CHARSET_GBK);
		Console.log(unzip);
	}

	@Test
	@Ignore
	public void unzipFromStreamTest() {
		File unzip = ZipUtil.unzip(FileUtil.getInputStream("e:/test/icefrog-core-5.1.0.jar"), FileUtil.file("e:/test/"), CharsetUtil.CHARSET_UTF_8);
		Console.log(unzip);
	}

	@Test
	@Ignore
	public void unzipChineseTest() {
		ZipUtil.unzip("d:/测试.zip");
	}

	@Test
	@Ignore
	public void unzipFileBytesTest() {
		byte[] fileBytes = ZipUtil.unzipFileBytes(FileUtil.file("e:/02 电力相关设备及服务2-241-.zip"), CharsetUtil.CHARSET_GBK, "images/CE-EP-HY-MH01-ES-0001.jpg");
		Assert.assertNotNull(fileBytes);
	}

	@Test
	public void gzipTest() {
		String data = "我是一个需要压缩的很长很长的字符串";
		byte[] bytes = StrUtil.utf8Bytes(data);
		byte[] gzip = ZipUtil.gzip(bytes);

		//保证gzip长度正常
		Assert.assertEquals(68, gzip.length);

		byte[] unGzip = ZipUtil.unGzip(gzip);
		//保证正常还原
		Assert.assertEquals(data, StrUtil.utf8Str(unGzip));
	}

	@Test
	public void zlibTest() {
		String data = "我是一个需要压缩的很长很长的字符串";
		byte[] bytes = StrUtil.utf8Bytes(data);
		byte[] gzip = ZipUtil.zlib(bytes, 0);

		//保证zlib长度正常
		Assert.assertEquals(62, gzip.length);
		byte[] unGzip = ZipUtil.unZlib(gzip);
		//保证正常还原
		Assert.assertEquals(data, StrUtil.utf8Str(unGzip));

		gzip = ZipUtil.zlib(bytes, 9);
		//保证zlib长度正常
		Assert.assertEquals(56, gzip.length);
		byte[] unGzip2 = ZipUtil.unZlib(gzip);
		//保证正常还原
		Assert.assertEquals(data, StrUtil.utf8Str(unGzip2));
	}

	@Test
	@Ignore
	public void zipStreamTest(){
		//https://github.com/whaleal/icefrog/issues/944
		String dir = "d:/test";
		String zip = "d:/test.zip";
		try (OutputStream out = new FileOutputStream(zip)){
			//实际应用中, out 为 HttpServletResponse.getOutputStream
			ZipUtil.zip(out, Charset.defaultCharset(), false, null, new File(dir));
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

}
