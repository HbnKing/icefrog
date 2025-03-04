package com.whaleal.icefrog.crypto.test.digest;

import org.junit.Assert;
import org.junit.Test;

import com.whaleal.icefrog.crypto.digest.MD5;

/**
 * MD5 单元测试
 *
 * @author Looly
 * @author wh
 *
 */
public class Md5Test {

	@Test
	public void md5To16Test() {
		String hex16 = new MD5().digestHex16("中国");
		Assert.assertEquals(16, hex16.length());
		Assert.assertEquals("cb143acd6c929826", hex16);
	}
}
