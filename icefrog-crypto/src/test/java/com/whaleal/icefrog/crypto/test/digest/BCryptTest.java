package com.whaleal.icefrog.crypto.test.digest;

import com.whaleal.icefrog.crypto.digest.BCrypt;
import org.junit.Assert;
import org.junit.Test;

public class BCryptTest {

	@Test
	public void checkpwTest(){
		Assert.assertFalse(BCrypt.checkpw("xxx",
				"$2a$2a$10$e4lBTlZ019KhuAFyqAlgB.Jxc6cM66GwkSR/5/xXNQuHUItPLyhzy"));
	}
}
