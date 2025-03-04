package com.whaleal.icefrog.core.date;

import com.whaleal.icefrog.core.date.BetweenFormatter.Level;
import org.junit.Assert;
import org.junit.Test;

public class BetweenFormatterTest {

	@Test
	public void formatTest(){
		long betweenMs = DateUtil.betweenMs(DateUtil.parse("2017-01-01 22:59:59"), DateUtil.parse("2017-01-02 23:59:58"));
		BetweenFormatter formater = new BetweenFormatter(betweenMs, Level.MILLISECOND, 1);
		Assert.assertEquals(formater.toString(), "1天");
	}

	@Test
	public void formatBetweenTest(){
		long betweenMs = DateUtil.betweenMs(DateUtil.parse("2018-07-16 11:23:19"), DateUtil.parse("2018-07-16 11:23:20"));
		BetweenFormatter formater = new BetweenFormatter(betweenMs, Level.SECOND, 1);
		Assert.assertEquals(formater.toString(), "1秒");
	}

	@Test
	public void formatBetweenTest2(){
		long betweenMs = DateUtil.betweenMs(DateUtil.parse("2018-07-16 12:25:23"), DateUtil.parse("2018-07-16 11:23:20"));
		BetweenFormatter formater = new BetweenFormatter(betweenMs, Level.SECOND, 5);
		Assert.assertEquals(formater.toString(), "1小时2分3秒");
	}

	@Test
	public void formatTest2(){
		BetweenFormatter formater = new BetweenFormatter(584, Level.SECOND, 1);
		Assert.assertEquals(formater.toString(), "0秒");
	}
}
