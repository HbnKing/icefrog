package com.whaleal.icefrog.extra.expression.engine.jfireel;

import com.whaleal.icefrog.extra.expression.ExpressionEngine;
import com.jfirer.jfireel.expression.Expression;

import java.util.Map;

/**
 * JfireEL引擎封装<br>
 * 见：https://github.com/eric_ds/jfireEL
 *
 * @since 1.0.0
 * @author Looly 
 * @author wh
 */
public class JfireELEngine implements ExpressionEngine {

	/**
	 * 构造
	 */
	public JfireELEngine(){
	}

	@Override
	public Object eval(String expression, Map<String, Object> context) {
		return Expression.parse(expression).calculate(context);
	}
}
