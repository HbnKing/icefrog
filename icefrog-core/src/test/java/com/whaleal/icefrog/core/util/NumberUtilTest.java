package com.whaleal.icefrog.core.util;

import com.whaleal.icefrog.core.convert.Convert;
import com.whaleal.icefrog.core.lang.Console;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Set;

/**
 * {@link NumberUtil} 单元测试类
 *
 * @author Looly
 * @author wh
 *
 */
public class NumberUtilTest {

	@Test
	public void addTest() {
		Float a = 3.15f;
		Double b = 4.22;
		double result = NumberUtil.add(a, b).doubleValue();
		Assert.assertEquals(7.37, result, 2);
	}

	@Test
	public void addTest2() {
		double a = 3.15f;
		double b = 4.22;
		double result = NumberUtil.add(a, b);
		Assert.assertEquals(7.37, result, 2);
	}

	@Test
	public void addTest3() {
		float a = 3.15f;
		double b = 4.22;
		double result = NumberUtil.add(a, b, a, b).doubleValue();
		Assert.assertEquals(14.74, result, 2);
	}

	@Test
	public void addTest4() {
		BigDecimal result = NumberUtil.add(new BigDecimal("133"), new BigDecimal("331"));
		Assert.assertEquals(new BigDecimal("464"), result);
	}

	@Test
	public void addBlankTest(){
		BigDecimal result = NumberUtil.add("123", " ");
		Assert.assertEquals(new BigDecimal("123"), result);
	}

	@Test
	public void isIntegerTest() {
		Assert.assertTrue(NumberUtil.isInteger("-12"));
		Assert.assertTrue(NumberUtil.isInteger("256"));
		Assert.assertTrue(NumberUtil.isInteger("0256"));
		Assert.assertTrue(NumberUtil.isInteger("0"));
		Assert.assertFalse(NumberUtil.isInteger("23.4"));
	}

	@Test
	public void isLongTest() {
		Assert.assertTrue(NumberUtil.isLong("-12"));
		Assert.assertTrue(NumberUtil.isLong("256"));
		Assert.assertTrue(NumberUtil.isLong("0256"));
		Assert.assertTrue(NumberUtil.isLong("0"));
		Assert.assertFalse(NumberUtil.isLong("23.4"));
	}

	@Test
	public void isNumberTest() {
		Assert.assertTrue(NumberUtil.isNumber("28.55"));
		Assert.assertTrue(NumberUtil.isNumber("0"));
		Assert.assertTrue(NumberUtil.isNumber("+100.10"));
		Assert.assertTrue(NumberUtil.isNumber("-22.022"));
		Assert.assertTrue(NumberUtil.isNumber("0X22"));
	}

	@Test
	public void divTest() {
		double result = NumberUtil.div(0, 1);
		Assert.assertEquals(0.0, result, 0);
	}

	@Test
	public void roundTest() {

		// 四舍
		String round1 = NumberUtil.roundStr(2.674, 2);
		String round2 = NumberUtil.roundStr("2.674", 2);
		Assert.assertEquals("2.67", round1);
		Assert.assertEquals("2.67", round2);

		// 五入
		String round3 = NumberUtil.roundStr(2.675, 2);
		String round4 = NumberUtil.roundStr("2.675", 2);
		Assert.assertEquals("2.68", round3);
		Assert.assertEquals("2.68", round4);

		// 四舍六入五成双
		String round31 = NumberUtil.roundStr(4.245, 2, RoundingMode.HALF_EVEN);
		String round41 = NumberUtil.roundStr("4.2451", 2, RoundingMode.HALF_EVEN);
		Assert.assertEquals("4.24", round31);
		Assert.assertEquals("4.25", round41);

		// 补0
		String round5 = NumberUtil.roundStr(2.6005, 2);
		String round6 = NumberUtil.roundStr("2.6005", 2);
		Assert.assertEquals("2.60", round5);
		Assert.assertEquals("2.60", round6);

		// 补0
		String round7 = NumberUtil.roundStr(2.600, 2);
		String round8 = NumberUtil.roundStr("2.600", 2);
		Assert.assertEquals("2.60", round7);
		Assert.assertEquals("2.60", round8);
	}

	@Test
	public void roundStrTest() {
		String roundStr = NumberUtil.roundStr(2.647, 2);
		Assert.assertEquals(roundStr, "2.65");
	}

	@Test
	public void roundHalfEvenTest() {
		String roundStr = NumberUtil.roundHalfEven(4.245, 2).toString();
		Assert.assertEquals(roundStr, "4.24");
		roundStr = NumberUtil.roundHalfEven(4.2450, 2).toString();
		Assert.assertEquals(roundStr, "4.24");
		roundStr = NumberUtil.roundHalfEven(4.2451, 2).toString();
		Assert.assertEquals(roundStr, "4.25");
		roundStr = NumberUtil.roundHalfEven(4.2250, 2).toString();
		Assert.assertEquals(roundStr, "4.22");

		roundStr = NumberUtil.roundHalfEven(1.2050, 2).toString();
		Assert.assertEquals(roundStr, "1.20");
		roundStr = NumberUtil.roundHalfEven(1.2150, 2).toString();
		Assert.assertEquals(roundStr, "1.22");
		roundStr = NumberUtil.roundHalfEven(1.2250, 2).toString();
		Assert.assertEquals(roundStr, "1.22");
		roundStr = NumberUtil.roundHalfEven(1.2350, 2).toString();
		Assert.assertEquals(roundStr, "1.24");
		roundStr = NumberUtil.roundHalfEven(1.2450, 2).toString();
		Assert.assertEquals(roundStr, "1.24");
		roundStr = NumberUtil.roundHalfEven(1.2550, 2).toString();
		Assert.assertEquals(roundStr, "1.26");
		roundStr = NumberUtil.roundHalfEven(1.2650, 2).toString();
		Assert.assertEquals(roundStr, "1.26");
		roundStr = NumberUtil.roundHalfEven(1.2750, 2).toString();
		Assert.assertEquals(roundStr, "1.28");
		roundStr = NumberUtil.roundHalfEven(1.2850, 2).toString();
		Assert.assertEquals(roundStr, "1.28");
		roundStr = NumberUtil.roundHalfEven(1.2950, 2).toString();
		Assert.assertEquals(roundStr, "1.30");
	}

	@Test
	public void decimalFormatTest() {
		long c = 299792458;// 光速

		String format = NumberUtil.decimalFormat(",###", c);
		Assert.assertEquals("299,792,458", format);
	}

	@Test(expected = IllegalArgumentException.class)
	public void decimalFormatNaNTest(){
		Double a = 0D;
		Double b = 0D;

		Double c = a / b;
		Console.log(NumberUtil.decimalFormat("#%", c));
	}

	@Test(expected = IllegalArgumentException.class)
	public void decimalFormatNaNTest2(){
		Double a = 0D;
		Double b = 0D;

		Console.log(NumberUtil.decimalFormat("#%", a / b));
	}

	@Test
	public void decimalFormatDoubleTest() {
		Double c = 467.8101;

		String format = NumberUtil.decimalFormat("0.00", c);
		Assert.assertEquals("467.81", format);
	}

	@Test
	public void decimalFormatMoneyTest() {
		double c = 299792400.543534534;

		String format = NumberUtil.decimalFormatMoney(c);
		Assert.assertEquals("299,792,400.54", format);

		double value = 0.5;
		String money = NumberUtil.decimalFormatMoney(value);
		Assert.assertEquals("0.50", money);
	}

	@Test
	public void equalsTest() {
		Assert.assertTrue(NumberUtil.equals(new BigDecimal("0.00"), BigDecimal.ZERO));
	}

	@Test
	public void toBigDecimalTest() {
		double a = 3.14;

		BigDecimal bigDecimal = NumberUtil.toBigDecimal(a);
		Assert.assertEquals("3.14", bigDecimal.toString());

		bigDecimal = NumberUtil.toBigDecimal("1,234.55");
		Assert.assertEquals("1234.55", bigDecimal.toString());

		bigDecimal = NumberUtil.toBigDecimal("1,234.56D");
		Assert.assertEquals("1234.56", bigDecimal.toString());
	}

	@Test
	public void maxTest() {
		int max = NumberUtil.max(5,4,3,6,1);
		Assert.assertEquals(6, max);
	}

	@Test
	public void minTest() {
		int min = NumberUtil.min(5,4,3,6,1);
		Assert.assertEquals(1, min);
	}

	@Test
	public void parseIntTest() {
		int number = NumberUtil.parseInt("0xFF");
		Assert.assertEquals(255, number);

		// 0开头
		number = NumberUtil.parseInt("010");
		Assert.assertEquals(10, number);

		number = NumberUtil.parseInt("10");
		Assert.assertEquals(10, number);

		number = NumberUtil.parseInt("   ");
		Assert.assertEquals(0, number);

		number = NumberUtil.parseInt("10F");
		Assert.assertEquals(10, number);

		number = NumberUtil.parseInt("22.4D");
		Assert.assertEquals(22, number);

		number = NumberUtil.parseInt("22.6D");
		Assert.assertEquals(22, number);

		number = NumberUtil.parseInt("0");
		Assert.assertEquals(0, number);

		number = NumberUtil.parseInt(".123");
		Assert.assertEquals(0, number);
	}

	@Test
	public void parseIntTest2() {
		// from 5.4.8 issue#I23ORQ@github
		// 千位分隔符去掉
		int v1 = NumberUtil.parseInt("1,482.00");
		Assert.assertEquals(1482, v1);
	}

	@Test
	public void parseNumberTest() {
		// from 5.4.8 issue#I23ORQ@github
		// 千位分隔符去掉
		int v1 = NumberUtil.parseNumber("1,482.00").intValue();
		Assert.assertEquals(1482, v1);

		Number v2 = NumberUtil.parseNumber("1,482.00D");
		Assert.assertEquals(1482L, v2.longValue());
	}

	@Test
	public void parseLongTest() {
		long number = NumberUtil.parseLong("0xFF");
		Assert.assertEquals(255, number);

		// 0开头
		number = NumberUtil.parseLong("010");
		Assert.assertEquals(10, number);

		number = NumberUtil.parseLong("10");
		Assert.assertEquals(10, number);

		number = NumberUtil.parseLong("   ");
		Assert.assertEquals(0, number);

		number = NumberUtil.parseLong("10F");
		Assert.assertEquals(10, number);

		number = NumberUtil.parseLong("22.4D");
		Assert.assertEquals(22, number);

		number = NumberUtil.parseLong("22.6D");
		Assert.assertEquals(22, number);

		number = NumberUtil.parseLong("0");
		Assert.assertEquals(0, number);

		number = NumberUtil.parseLong(".123");
		Assert.assertEquals(0, number);
	}

	@Test
	public void factorialTest(){
		long factorial = NumberUtil.factorial(0);
		Assert.assertEquals(1, factorial);

		Assert.assertEquals(1L, NumberUtil.factorial(1));
		Assert.assertEquals(1307674368000L, NumberUtil.factorial(15));
		Assert.assertEquals(2432902008176640000L, NumberUtil.factorial(20));

		factorial = NumberUtil.factorial(5, 0);
		Assert.assertEquals(120, factorial);
		factorial = NumberUtil.factorial(5, 1);
		Assert.assertEquals(120, factorial);

		Assert.assertEquals(5, NumberUtil.factorial(5, 4));
		Assert.assertEquals(2432902008176640000L, NumberUtil.factorial(20, 0));
	}

	@Test
	public void factorialTest2(){
		long factorial = NumberUtil.factorial(new BigInteger("0")).longValue();
		Assert.assertEquals(1, factorial);

		Assert.assertEquals(1L, NumberUtil.factorial(new BigInteger("1")).longValue());
		Assert.assertEquals(1307674368000L, NumberUtil.factorial(new BigInteger("15")).longValue());
		Assert.assertEquals(2432902008176640000L, NumberUtil.factorial(20));

		factorial = NumberUtil.factorial(new BigInteger("5"), new BigInteger("0")).longValue();
		Assert.assertEquals(120, factorial);
		factorial = NumberUtil.factorial(new BigInteger("5"), BigInteger.ONE).longValue();
		Assert.assertEquals(120, factorial);

		Assert.assertEquals(5, NumberUtil.factorial(new BigInteger("5"), new BigInteger("4")).longValue());
		Assert.assertEquals(2432902008176640000L, NumberUtil.factorial(new BigInteger("20"), BigInteger.ZERO).longValue());
	}

	@Test
	public void mulTest(){
		final BigDecimal mul = NumberUtil.mul(new BigDecimal("10"), null);
		Assert.assertEquals(BigDecimal.ZERO, mul);
	}


	@Test
	public void isPowerOfTwoTest() {
		Assert.assertFalse(NumberUtil.isPowerOfTwo(-1));
		Assert.assertTrue(NumberUtil.isPowerOfTwo(16));
		Assert.assertTrue(NumberUtil.isPowerOfTwo(65536));
		Assert.assertTrue(NumberUtil.isPowerOfTwo(1));
		Assert.assertFalse(NumberUtil.isPowerOfTwo(17));
	}

	@Test
	public void generateRandomNumberTest(){
		final int[] ints = NumberUtil.generateRandomNumber(10, 20, 5);
		Assert.assertEquals(5, ints.length);
		final Set<?> set = Convert.convert(Set.class, ints);
		Assert.assertEquals(5, set.size());
	}

	@Test
	public void toStrTest(){
		Assert.assertEquals("1", NumberUtil.toStr(new BigDecimal("1.0000000000")));
		Assert.assertEquals("0", NumberUtil.toStr(NumberUtil.sub(new BigDecimal("9600.00000"), new BigDecimal("9600.00000"))));
		Assert.assertEquals("0", NumberUtil.toStr(NumberUtil.sub(new BigDecimal("9600.0000000000"), new BigDecimal("9600.000000"))));
		Assert.assertEquals("0", NumberUtil.toStr(new BigDecimal("9600.00000").subtract(new BigDecimal("9600.000000000"))));
	}

	@Test
	public void generateRandomNumberTest2(){
		// 检查边界
		final int[] ints = NumberUtil.generateRandomNumber(1, 8, 7);
		Assert.assertEquals(7, ints.length);
		final Set<?> set = Convert.convert(Set.class, ints);
		Assert.assertEquals(7, set.size());
	}
}
