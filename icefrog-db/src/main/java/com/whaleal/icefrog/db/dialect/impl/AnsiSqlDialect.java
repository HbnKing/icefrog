package com.whaleal.icefrog.db.dialect.impl;

import com.whaleal.icefrog.core.lang.Preconditions;
import com.whaleal.icefrog.core.util.ArrayUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.db.DbRuntimeException;
import com.whaleal.icefrog.db.Entity;
import com.whaleal.icefrog.db.Page;
import com.whaleal.icefrog.db.StatementUtil;
import com.whaleal.icefrog.db.dialect.Dialect;
import com.whaleal.icefrog.db.dialect.DialectName;
import com.whaleal.icefrog.db.sql.Condition;
import com.whaleal.icefrog.db.sql.Query;
import com.whaleal.icefrog.db.sql.SqlBuilder;
import com.whaleal.icefrog.db.sql.Wrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * ANSI SQL 方言
 *
 * @author Looly
 * @author wh
 *
 */
public class AnsiSqlDialect implements Dialect {
	private static final long serialVersionUID = 2088101129774974580L;

	protected Wrapper wrapper = new Wrapper();

	@Override
	public Wrapper getWrapper() {
		return this.wrapper;
	}

	@Override
	public void setWrapper(Wrapper wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public PreparedStatement psForInsert(Connection conn, Entity entity) throws SQLException {
		final SqlBuilder insert = SqlBuilder.create(wrapper).insert(entity, this.dialectName());

		return StatementUtil.prepareStatement(conn, insert);
	}

	@Override
	public PreparedStatement psForInsertBatch(Connection conn, Entity... entities) throws SQLException {
		if (ArrayUtil.isEmpty(entities)) {
			throw new DbRuntimeException("Entities for batch insert is empty !");
		}
		// 批量，根据第一行数据结构生成SQL占位符
		final SqlBuilder insert = SqlBuilder.create(wrapper).insert(entities[0], this.dialectName());
		return StatementUtil.prepareStatementForBatch(conn, insert.build(), insert.getFields(), entities);
	}

	@Override
	public PreparedStatement psForDelete(Connection conn, Query query) throws SQLException {
		Preconditions.notNull(query, "query must be not null !");

		final Condition[] where = query.getWhere();
		if (ArrayUtil.isEmpty(where)) {
			// 对于无条件的删除语句直接抛出异常禁止，防止误删除
			throw new SQLException("No 'WHERE' condition, we can't prepared statement for delete everything.");
		}
		final SqlBuilder delete = SqlBuilder.create(wrapper).delete(query.getFirstTableName()).where(where);

		return StatementUtil.prepareStatement(conn, delete);
	}

	@Override
	public PreparedStatement psForUpdate(Connection conn, Entity entity, Query query) throws SQLException {
		Preconditions.notNull(query, "query must be not null !");

		final Condition[] where = query.getWhere();
		if (ArrayUtil.isEmpty(where)) {
			// 对于无条件的删除语句直接抛出异常禁止，防止误删除
			throw new SQLException("No 'WHERE' condition, we can't prepare statement for update everything.");
		}

		final SqlBuilder update = SqlBuilder.create(wrapper).update(entity).where(where);

		return StatementUtil.prepareStatement(conn, update);
	}

	@Override
	public PreparedStatement psForFind(Connection conn, Query query) throws SQLException {
		return psForPage(conn, query);
	}

	@Override
	public PreparedStatement psForPage(Connection conn, Query query) throws SQLException {
		Preconditions.notNull(query, "query must be not null !");
		if (StrUtil.hasBlank(query.getTableNames())) {
			throw new DbRuntimeException("Table name must be not empty !");
		}

		final SqlBuilder find = SqlBuilder.create(wrapper).query(query);
		return psForPage(conn, find, query.getPage());
	}

	@Override
	public PreparedStatement psForPage(Connection conn, SqlBuilder sqlBuilder, Page page) throws SQLException {
		// 根据不同数据库在查询SQL语句基础上包装其分页的语句
		if(null != page){
			sqlBuilder = wrapPageSql(sqlBuilder.orderBy(page.getOrders()), page);
		}
		return StatementUtil.prepareStatement(conn, sqlBuilder);
	}

	/**
	 * 根据不同数据库在查询SQL语句基础上包装其分页的语句<br>
	 * 各自数据库通过重写此方法实现最小改动情况下修改分页语句
	 *
	 * @param find 标准查询语句
	 * @param page 分页对象
	 * @return 分页语句
	 * @since 1.0.0
	 */
	protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
		// limit A offset B 表示：A就是你需要多少行，B就是查询的起点位置。
		return find
				.append(" limit ")
				.append(page.getPageSize())
				.append(" offset ")
				.append(page.getStartPosition());
	}

	@Override
	public String dialectName() {
		return DialectName.ANSI.name();
	}

	// ---------------------------------------------------------------------------- Protected method start
	// ---------------------------------------------------------------------------- Protected method end
}
