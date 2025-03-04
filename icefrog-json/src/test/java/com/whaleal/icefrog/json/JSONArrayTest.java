package com.whaleal.icefrog.json;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.collection.ListUtil;
import com.whaleal.icefrog.core.convert.ConvertException;
import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.lang.Console;
import com.whaleal.icefrog.core.lang.Dict;
import com.whaleal.icefrog.core.lang.TypeReference;
import com.whaleal.icefrog.core.util.CharsetUtil;
import com.whaleal.icefrog.json.test.bean.Exam;
import com.whaleal.icefrog.json.test.bean.JsonNode;
import com.whaleal.icefrog.json.test.bean.KeyBean;
import lombok.Data;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSONArray单元测试
 *
 * @author Looly
 * @author wh
 *
 */
public class JSONArrayTest {

	@Test(expected = JSONException.class)
	public void createJSONArrayTest(){
		// 集合类不支持转为JSONObject
		new JSONArray(new JSONObject(), JSONConfig.create());
	}

	@Test
	public void addNullTest(){
		final List<String> aaa = ListUtil.of("aaa", null);
		String jsonStr = JSONUtil.toJsonStr(JSONUtil.parse(aaa,
				JSONConfig.create().setIgnoreNullValue(false)));
		Assert.assertEquals("[\"aaa\",null]", jsonStr);
	}

	@Test
	public void addTest() {
		// 方法1
		JSONArray array = JSONUtil.createArray();
		// 方法2
		// JSONArray array = new JSONArray();
		array.add("value1");
		array.add("value2");
		array.add("value3");

		Assert.assertEquals(array.get(0), "value1");
	}

	@Test
	public void parseTest() {
		String jsonStr = "[\"value1\", \"value2\", \"value3\"]";
		JSONArray array = JSONUtil.parseArray(jsonStr);
		Assert.assertEquals(array.get(0), "value1");
	}

	@Test
	public void parseWithNullTest() {
		String jsonStr = "[{\"grep\":\"4.8\",\"result\":\"右\"},{\"grep\":\"4.8\",\"result\":null}]";
		JSONArray jsonArray = JSONUtil.parseArray(jsonStr);
		Assert.assertFalse(jsonArray.getJSONObject(1).containsKey("result"));

		// 不忽略null，则null的键值对被保留
		jsonArray = new JSONArray(jsonStr, false);
		Assert.assertTrue(jsonArray.getJSONObject(1).containsKey("result"));
	}

	@Test
	public void parseFileTest() {
		JSONArray array = JSONUtil.readJSONArray(FileUtil.file("exam_test.json"), CharsetUtil.CHARSET_UTF_8);

		JSONObject obj0 = array.getJSONObject(0);
		Exam exam = JSONUtil.toBean(obj0, Exam.class);
		Assert.assertEquals("0", exam.getAnswerArray()[0].getSeq());
	}

	@Test
	@Ignore
	public void parseBeanListTest() {
		KeyBean b1 = new KeyBean();
		b1.setAkey("aValue1");
		b1.setBkey("bValue1");
		KeyBean b2 = new KeyBean();
		b2.setAkey("aValue2");
		b2.setBkey("bValue2");

		ArrayList<KeyBean> list = CollUtil.newArrayList(b1, b2);

		JSONArray jsonArray = JSONUtil.parseArray(list);
		Console.log(jsonArray);
	}

	@Test
	public void toListTest() {
		String jsonStr = FileUtil.readString("exam_test.json", CharsetUtil.CHARSET_UTF_8);
		JSONArray array = JSONUtil.parseArray(jsonStr);

		List<Exam> list = array.toList(Exam.class);
		Assert.assertFalse(list.isEmpty());
		Assert.assertSame(Exam.class, list.get(0).getClass());
	}

	@Test
	public void toListTest2() {
		String jsonArr = "[{\"id\":111,\"name\":\"test1\"},{\"id\":112,\"name\":\"test2\"}]";

		JSONArray array = JSONUtil.parseArray(jsonArr);
		List<User> userList = JSONUtil.toList(array, User.class);

		Assert.assertFalse(userList.isEmpty());
		Assert.assertSame(User.class, userList.get(0).getClass());

		Assert.assertEquals(Integer.valueOf(111), userList.get(0).getId());
		Assert.assertEquals(Integer.valueOf(112), userList.get(1).getId());

		Assert.assertEquals("test1", userList.get(0).getName());
		Assert.assertEquals("test2", userList.get(1).getName());
	}

	@Test
	public void toDictListTest() {
		String jsonArr = "[{\"id\":111,\"name\":\"test1\"},{\"id\":112,\"name\":\"test2\"}]";

		JSONArray array = JSONUtil.parseArray(jsonArr);

		List<Dict> list = JSONUtil.toList(array, Dict.class);

		Assert.assertFalse(list.isEmpty());
		Assert.assertSame(Dict.class, list.get(0).getClass());

		Assert.assertEquals(Integer.valueOf(111), list.get(0).getInt("id"));
		Assert.assertEquals(Integer.valueOf(112), list.get(1).getInt("id"));

		Assert.assertEquals("test1", list.get(0).getStr("name"));
		Assert.assertEquals("test2", list.get(1).getStr("name"));
	}

	@Test
	public void toArrayTest() {
		String jsonStr = FileUtil.readString("exam_test.json", CharsetUtil.CHARSET_UTF_8);
		JSONArray array = JSONUtil.parseArray(jsonStr);

		//noinspection SuspiciousToArrayCall
		Exam[] list = array.toArray(new Exam[0]);
		Assert.assertNotEquals(0, list.length);
		Assert.assertSame(Exam.class, list[0].getClass());
	}

	/**
	 * 单元测试用于测试在列表元素中有null时的情况下是否出错
	 */
	@Test
	public void toListWithNullTest() {
		String json = "[null,{'akey':'avalue','bkey':'bvalue'}]";
		JSONArray ja = JSONUtil.parseArray(json);

		List<KeyBean> list = ja.toList(KeyBean.class);
		Assert.assertNull(list.get(0));
		Assert.assertEquals("avalue", list.get(1).getAkey());
		Assert.assertEquals("bvalue", list.get(1).getBkey());
	}

	@Test(expected = ConvertException.class)
	public void toListWithErrorTest(){
		String json = "[['aaa',{'akey':'avalue','bkey':'bvalue'}]]";
		JSONArray ja = JSONUtil.parseArray(json);

		ja.toBean(new TypeReference<List<List<KeyBean>>>() {});
	}

	@Test
	public void toBeanListTest() {
		List<Map<String, String>> mapList = new ArrayList<>();
		mapList.add(buildMap("0", "0", "0"));
		mapList.add(buildMap("1", "1", "1"));
		mapList.add(buildMap("+0", "+0", "+0"));
		mapList.add(buildMap("-0", "-0", "-0"));
		JSONArray jsonArray = JSONUtil.parseArray(mapList);
		List<JsonNode> nodeList = jsonArray.toList(JsonNode.class);

		Assert.assertEquals(Long.valueOf(0L), nodeList.get(0).getId());
		Assert.assertEquals(Long.valueOf(1L), nodeList.get(1).getId());
		Assert.assertEquals(Long.valueOf(0L), nodeList.get(2).getId());
		Assert.assertEquals(Long.valueOf(0L), nodeList.get(3).getId());

		Assert.assertEquals(Integer.valueOf(0), nodeList.get(0).getParentId());
		Assert.assertEquals(Integer.valueOf(1), nodeList.get(1).getParentId());
		Assert.assertEquals(Integer.valueOf(0), nodeList.get(2).getParentId());
		Assert.assertEquals(Integer.valueOf(0), nodeList.get(3).getParentId());

		Assert.assertEquals("0", nodeList.get(0).getName());
		Assert.assertEquals("1", nodeList.get(1).getName());
		Assert.assertEquals("+0", nodeList.get(2).getName());
		Assert.assertEquals("-0", nodeList.get(3).getName());
	}

	@Test
	public void getByPathTest(){
		String jsonStr = "[{\"id\": \"1\",\"name\": \"a\"},{\"id\": \"2\",\"name\": \"b\"}]";
		final JSONArray jsonArray = JSONUtil.parseArray(jsonStr);
		Assert.assertEquals("b", jsonArray.getByPath("[1].name"));
		Assert.assertEquals("b", JSONUtil.getByPath(jsonArray, "[1].name"));
	}

	@Test
	public void putTest(){
		final JSONArray jsonArray = new JSONArray();
		jsonArray.put(3, "test");
		// 第三个位置插入值，0~2都是null
		Assert.assertEquals(4, jsonArray.size());
	}

	private static Map<String, String> buildMap(String id, String parentId, String name) {
		Map<String, String> map = new HashMap<>();
		map.put("id", id);
		map.put("parentId", parentId);
		map.put("name", name);
		return map;
	}

	@Data
	static class User {
		private Integer id;
		private String name;
	}
}
