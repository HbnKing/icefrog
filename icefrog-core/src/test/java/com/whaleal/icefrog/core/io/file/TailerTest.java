package com.whaleal.icefrog.core.io.file;

import org.junit.Ignore;
import org.junit.Test;

import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.util.CharsetUtil;

public class TailerTest {

	@Test
	@Ignore
	public void tailTest() {
		FileUtil.tail(FileUtil.file("e:/tail.txt"), CharsetUtil.CHARSET_GBK);
	}

	@Test
	@Ignore
	public void tailWithLinesTest() {
		Tailer tailer = new Tailer(FileUtil.file("f:/test/test.log"), Tailer.CONSOLE_HANDLER, 2);
		tailer.start();
	}
}
