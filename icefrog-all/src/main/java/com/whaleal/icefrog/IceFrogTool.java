/*
 * Copyright (C) 2017 icefrog.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whaleal.icefrog;

import com.whaleal.icefrog.core.lang.ConsoleTable;
import com.whaleal.icefrog.core.util.ClassUtil;
import com.whaleal.icefrog.core.util.StrUtil;

import java.util.Set;

/**
 * <p>
 *     秋千水，竹马道，一眼见你，万物不及。<br>
 *     春水生，春林初胜，春风十里不如你。
 * </p>
 *
 * <p>
 * whaleal icefrog是一个小而全的Java工具类库，通过静态方法封装，降低相关API的学习成本，提高工作效率，使Java拥有函数式语言般的优雅，让Java语言也可以“甜甜的”。
 * </p>
 *
 * <p>
 * whaleal icefrog中的工具方法来自于每个用户的精雕细琢，它涵盖了Java开发底层代码中的方方面面，它既是大型项目开发中解决小问题的利器，也是小型项目中的效率担当；<br>
 * </p>
 *
 * <p>whaleal icefrog是项目中“util”包友好的替代，它节省了开发人员对项目中公用类和公用工具方法的封装时间，使开发专注于业务，同时可以最大限度的避免封装不完善带来的bug。</p>
 *
 * @author Looly
 * @author wh
 */
public class IceFrogTool {

	public static final String AUTHOR = "wh";

	private IceFrogTool() {
	}

	/**
	 * 显示icefrog所有的工具类
	 *
	 * @return 工具类名集合
	 * @since 1.0.0
	 */
	public static Set<Class<?>> getAllUtils() {
		return ClassUtil.scanPackage("com.whaleal.icefrog",
				(clazz) -> (false == clazz.isInterface()) && StrUtil.endWith(clazz.getSimpleName(), "Util"));
	}

	/**
	 * 控制台打印所有工具类
	 */
	public static void printAllUtils() {
		final Set<Class<?>> allUtils = getAllUtils();
		final ConsoleTable consoleTable = ConsoleTable.create().addHeader("工具类名", "所在包");
		for (Class<?> clazz : allUtils) {
			consoleTable.addBody(clazz.getSimpleName(), clazz.getPackage().getName());
		}
		consoleTable.print();
	}
}
