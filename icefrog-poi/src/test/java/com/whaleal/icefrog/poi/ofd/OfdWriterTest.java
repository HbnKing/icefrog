package com.whaleal.icefrog.poi.ofd;

import com.whaleal.icefrog.core.io.FileUtil;
import org.junit.Ignore;
import org.junit.Test;

public class OfdWriterTest {

	@Test
	@Ignore
	public void writeTest(){
		final OfdWriter ofdWriter = new OfdWriter(FileUtil.file("d:/test/test.ofd"));
		ofdWriter.addText(null, "测试文本");
		ofdWriter.close();
	}
}
