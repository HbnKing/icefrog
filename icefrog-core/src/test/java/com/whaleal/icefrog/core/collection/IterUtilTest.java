package com.whaleal.icefrog.core.collection;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * {@link IterUtil} 单元测试
 * @author Looly
 * @author wh
 *
 */
public class IterUtilTest {

	@Test
	public void getFirstNonNullTest(){
		final ArrayList<String> strings = CollUtil.newArrayList(null, null, "123", "456", null);
		Assert.assertEquals("123", IterUtil.getFirstNoneNull(strings));
	}

	@Test
	public void fieldValueMapTest() {
		ArrayList<Car> carList = CollUtil.newArrayList(new Car("123", "大众"), new Car("345", "奔驰"), new Car("567", "路虎"));
		Map<String, Car> carNameMap = IterUtil.fieldValueMap(carList.iterator(), "carNumber");

		Assert.assertEquals("大众", carNameMap.get("123").getCarName());
		Assert.assertEquals("奔驰", carNameMap.get("345").getCarName());
		Assert.assertEquals("路虎", carNameMap.get("567").getCarName());
	}

	@Test
	public void joinTest() {
		ArrayList<String> list = CollUtil.newArrayList("1", "2", "3", "4");
		String join = IterUtil.join(list.iterator(), ":");
		Assert.assertEquals("1:2:3:4", join);

		ArrayList<Integer> list1 = CollUtil.newArrayList(1, 2, 3, 4);
		String join1 = IterUtil.join(list1.iterator(), ":");
		Assert.assertEquals("1:2:3:4", join1);

		// 包装每个节点
		ArrayList<String> list2 = CollUtil.newArrayList("1", "2", "3", "4");
		String join2 = IterUtil.join(list2.iterator(), ":", "\"", "\"");
		Assert.assertEquals("\"1\":\"2\":\"3\":\"4\"", join2);
	}

	@Test
	public void joinWithFuncTest() {
		ArrayList<String> list = CollUtil.newArrayList("1", "2", "3", "4");
		String join = IterUtil.join(list.iterator(), ":", String::valueOf);
		Assert.assertEquals("1:2:3:4", join);
	}

	@Test
	public void joinWithNullTest() {
		ArrayList<String> list = CollUtil.newArrayList("1", null, "3", "4");
		String join = IterUtil.join(list.iterator(), ":", String::valueOf);
		Assert.assertEquals("1:null:3:4", join);
	}

	@Test
	public void testToListMap() {
		Map<String, List<String>> expectedMap = new HashMap<>();
		expectedMap.put("a", Collections.singletonList("and"));
		expectedMap.put("b", Arrays.asList("brave", "back"));

		Map<String, List<String>> testMap = IterUtil.toListMap(Arrays.asList("and", "brave", "back"),
				v -> v.substring(0, 1));
		Assert.assertEquals(testMap, expectedMap);
	}

	@Test
	public void testToMap() {
		Map<String, Car> expectedMap = new HashMap<>();

		Car bmw = new Car("123", "bmw");
		expectedMap.put("123", bmw);

		Car benz = new Car("456", "benz");
		expectedMap.put("456", benz);

		Map<String, Car> testMap = IterUtil.toMap(Arrays.asList(bmw, benz), Car::getCarNumber);
		Assert.assertEquals(expectedMap, testMap);
	}

	public static class Car {
		private String carNumber;
		private String carName;

		public Car(String carNumber, String carName) {
			this.carNumber = carNumber;
			this.carName = carName;
		}

		public String getCarNumber() {
			return carNumber;
		}

		public void setCarNumber(String carNumber) {
			this.carNumber = carNumber;
		}

		public String getCarName() {
			return carName;
		}

		public void setCarName(String carName) {
			this.carName = carName;
		}
	}
}
