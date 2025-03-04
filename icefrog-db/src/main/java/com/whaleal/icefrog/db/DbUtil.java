package com.whaleal.icefrog.db;

import com.whaleal.icefrog.core.convert.Convert;
import com.whaleal.icefrog.core.io.IoUtil;
import com.whaleal.icefrog.db.dialect.Dialect;
import com.whaleal.icefrog.db.dialect.DialectFactory;
import com.whaleal.icefrog.db.ds.DSFactory;
import com.whaleal.icefrog.db.sql.SqlLog;
import com.whaleal.icefrog.log.Log;
import com.whaleal.icefrog.log.level.Level;
import com.whaleal.icefrog.setting.Setting;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 数据库操作工具类
 *
 * @author Looly
 * @author wh
 */
public final class DbUtil {
	private final static Log log = Log.get();

	/**
	 * 实例化一个新的SQL运行对象
	 *
	 * @param dialect 数据源
	 * @return SQL执行类
	 */
	public static SqlConnRunner newSqlConnRunner(Dialect dialect) {
		return SqlConnRunner.create(dialect);
	}

	/**
	 * 实例化一个新的SQL运行对象
	 *
	 * @param ds 数据源
	 * @return SQL执行类
	 */
	public static SqlConnRunner newSqlConnRunner(DataSource ds) {
		return SqlConnRunner.create(ds);
	}

	/**
	 * 实例化一个新的SQL运行对象
	 *
	 * @param conn 数据库连接对象
	 * @return SQL执行类
	 */
	public static SqlConnRunner newSqlConnRunner(Connection conn) {
		return SqlConnRunner.create(DialectFactory.newDialect(conn));
	}

	/**
	 * 实例化一个新的Db，使用默认数据源
	 *
	 * @return SQL执行类
	 */
	public static Db use() {
		return Db.use();
	}

	/**
	 * 实例化一个新的Db对象
	 *
	 * @param ds 数据源
	 * @return SQL执行类
	 */
	public static Db use(DataSource ds) {
		return Db.use(ds);
	}

	/**
	 * 实例化一个新的SQL运行对象
	 *
	 * @param ds      数据源
	 * @param dialect SQL方言
	 * @return SQL执行类
	 */
	public static Db use(DataSource ds, Dialect dialect) {
		return Db.use(ds, dialect);
	}

	/**
	 * 新建数据库会话，使用默认数据源
	 *
	 * @return 数据库会话
	 */
	public static Session newSession() {
		return Session.create(getDs());
	}

	/**
	 * 新建数据库会话
	 *
	 * @param ds 数据源
	 * @return 数据库会话
	 */
	public static Session newSession(DataSource ds) {
		return Session.create(ds);
	}

	/**
	 * 连续关闭一系列的SQL相关对象<br>
	 * 这些对象必须按照顺序关闭，否则会出错。
	 *
	 * @param objsToClose 需要关闭的对象
	 */
	public static void close(Object... objsToClose) {
		for (Object obj : objsToClose) {
			if(null != obj){
				if (obj instanceof AutoCloseable) {
					IoUtil.close((AutoCloseable) obj);
				} else {
					log.warn("Object {} not a ResultSet or Statement or PreparedStatement or Connection!", obj.getClass().getName());
				}
			}
		}
	}

	/**
	 * 获得默认数据源
	 *
	 * @return 默认数据源
	 */
	public static DataSource getDs() {
		return DSFactory.get();
	}

	/**
	 * 获取指定分组的数据源
	 *
	 * @param group 分组
	 * @return 数据源
	 */
	public static DataSource getDs(String group) {
		return DSFactory.get(group);
	}

	/**
	 * 获得JNDI数据源
	 *
	 * @param jndiName JNDI名称
	 * @return 数据源
	 */
	public static DataSource getJndiDsWithLog(String jndiName) {
		try {
			return getJndiDs(jndiName);
		} catch (DbRuntimeException e) {
			log.error(e.getCause(), "Find JNDI datasource error!");
		}
		return null;
	}

	/**
	 * 获得JNDI数据源
	 *
	 * @param jndiName JNDI名称
	 * @return 数据源
	 */
	public static DataSource getJndiDs(String jndiName) {
		try {
			return (DataSource) new InitialContext().lookup(jndiName);
		} catch (NamingException e) {
			throw new DbRuntimeException(e);
		}
	}

	/**
	 * 移除配置文件中的Show SQL相关配置项<br>
	 * 此方法用于移除用户配置在分组下的配置项目
	 *
	 * @param setting 配置项
	 * @since 1.0.0
	 */
	public static void removeShowSqlParams(Setting setting){
		setting.remove(SqlLog.KEY_SHOW_SQL);
		setting.remove(SqlLog.KEY_FORMAT_SQL);
		setting.remove(SqlLog.KEY_SHOW_PARAMS);
		setting.remove(SqlLog.KEY_SQL_LEVEL);
	}

	/**
	 * 从配置文件中读取SQL打印选项，读取后会去除相应属性
	 *
	 * @param setting 配置文件
	 * @since 1.0.0
	 */
	public static void setShowSqlGlobal(Setting setting) {
		// 初始化SQL显示
		final boolean isShowSql = Convert.toBool(setting.remove(SqlLog.KEY_SHOW_SQL), false);
		final boolean isFormatSql = Convert.toBool(setting.remove(SqlLog.KEY_FORMAT_SQL), false);
		final boolean isShowParams = Convert.toBool(setting.remove(SqlLog.KEY_SHOW_PARAMS), false);
		String sqlLevelStr = setting.remove(SqlLog.KEY_SQL_LEVEL);
		if (null != sqlLevelStr) {
			sqlLevelStr = sqlLevelStr.toUpperCase();
		}
		final Level level = Convert.toEnum(Level.class, sqlLevelStr, Level.DEBUG);
		log.debug("Show sql: [{}], format sql: [{}], show params: [{}], level: [{}]", isShowSql, isFormatSql, isShowParams, level);
		setShowSqlGlobal(isShowSql, isFormatSql, isShowParams, level);
	}

	/**
	 * 设置全局配置：是否通过debug日志显示SQL
	 *
	 * @param isShowSql    是否显示SQL
	 * @param isFormatSql  是否格式化显示的SQL
	 * @param isShowParams 是否打印参数
	 * @param level        SQL打印到的日志等级
	 * @since 1.0.0
	 */
	public static void setShowSqlGlobal(boolean isShowSql, boolean isFormatSql, boolean isShowParams, Level level) {
		GlobalDbConfig.setShowSql(isShowSql, isFormatSql, isShowParams, level);
	}

	/**
	 * 设置全局是否在结果中忽略大小写<br>
	 * 如果忽略，则在Entity中调用getXXX时，字段值忽略大小写，默认忽略
	 *
	 * @param caseInsensitive 否在结果中忽略大小写
	 * @since 1.0.0
	 */
	public static void setCaseInsensitiveGlobal(boolean caseInsensitive) {
		GlobalDbConfig.setCaseInsensitive(caseInsensitive);
	}

	/**
	 * 设置全局是否INSERT语句中默认返回主键（默认返回主键）<br>
	 * 如果false，则在Insert操作后，返回影响行数
	 * 主要用于某些数据库不支持返回主键的情况
	 *
	 * @param returnGeneratedKey 是否INSERT语句中默认返回主键
	 * @since 1.0.0
	 */
	public static void setReturnGeneratedKeyGlobal(boolean returnGeneratedKey) {
		GlobalDbConfig.setReturnGeneratedKey(returnGeneratedKey);
	}
}
