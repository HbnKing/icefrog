package com.whaleal.icefrog.db;

import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;

import com.whaleal.icefrog.core.lang.Console;

/**
 * PostgreSQL 单元测试
 *
 * @author Looly
 * @author wh
 *
 */
public class PostgreTest {

	@Test
	@Ignore
	public void insertTest() throws SQLException {
		for (int id = 100; id < 200; id++) {
			Db.use("postgre").insert(Entity.create("user")//
					.set("id", id)//
					.set("name", "测试用户" + id)//
			);
		}
	}

	@Test
	@Ignore
	public void pageTest() throws SQLException {
		PageResult<Entity> result = Db.use("postgre").page(Entity.create("user"), new Page(2, 10));
		for (Entity entity : result) {
			Console.log(entity.get("id"));
		}
	}
}
