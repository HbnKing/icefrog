package com.whaleal.icefrog.core.lang;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.whaleal.icefrog.core.text.StrSplitter;

/**
 * {@link StrSplitter} 单元测试
 * @author Looly
 * @author wh
 *
 */
public class StrSpliterTest {

	@Test
	public void splitByCharTest(){
		String str1 = "a, ,efedsfs,   ddf";
		List<String> split = StrSplitter.split(str1, ',', 0, true, true);
		Assert.assertEquals("ddf", split.get(2));
		Assert.assertEquals(3, split.size());
	}

	@Test
	public void splitByStrTest(){
		String str1 = "aabbccaaddaaee";
		List<String> split = StrSplitter.split(str1, "aa", 0, true, true);
		Assert.assertEquals("ee", split.get(2));
		Assert.assertEquals(3, split.size());
	}

	@Test
	public void splitByBlankTest(){
		String str1 = "aa bbccaa     ddaaee";
		List<String> split = StrSplitter.split(str1, 0);
		Assert.assertEquals("ddaaee", split.get(2));
		Assert.assertEquals(3, split.size());
	}

	@Test
	public void splitPathTest(){
		String str1 = "/use/local/bin";
		List<String> split = StrSplitter.splitPath(str1, 0);
		Assert.assertEquals("bin", split.get(2));
		Assert.assertEquals(3, split.size());
	}
}
