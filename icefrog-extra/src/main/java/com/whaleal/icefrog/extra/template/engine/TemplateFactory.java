package com.whaleal.icefrog.extra.template.engine;

import com.whaleal.icefrog.core.lang.Singleton;
import com.whaleal.icefrog.core.util.ReflectUtil;
import com.whaleal.icefrog.core.util.ServiceLoaderUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.extra.template.TemplateConfig;
import com.whaleal.icefrog.extra.template.TemplateEngine;
import com.whaleal.icefrog.extra.template.TemplateException;
import com.whaleal.icefrog.log.StaticLog;

/**
 * 简单模板工厂，用于根据用户引入的模板引擎jar，自动创建对应的模板引擎对象
 *
 * @author Looly
 * @author wh
 */
public class TemplateFactory {

	/**
	 * 根据用户引入的模板引擎jar，自动创建对应的模板引擎对象<br>
	 * 获得的是单例的TemplateEngine
	 *
	 * @return 单例的TemplateEngine
	 */
	public static TemplateEngine get(){
		return Singleton.get(TemplateEngine.class.getName(), TemplateFactory::create);
	}

	/**
	 * 根据用户引入的模板引擎jar，自动创建对应的模板引擎对象<br>
	 * 推荐创建的引擎单例使用，此方法每次调用会返回新的引擎
	 *
	 * @return {@link TemplateEngine}
	 * @since 1.0.0
	 */
	public static TemplateEngine create() {
		return create(new TemplateConfig());
	}

	/**
	 * 根据用户引入的模板引擎jar，自动创建对应的模板引擎对象<br>
	 * 推荐创建的引擎单例使用，此方法每次调用会返回新的引擎
	 *
	 * @param config 模板配置，包括编码、模板文件path等信息
	 * @return {@link TemplateEngine}
	 */
	public static TemplateEngine create(TemplateConfig config) {
		final TemplateEngine engine = doCreate(config);
		StaticLog.debug("Use [{}] Engine As Default.", StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine"));
		return engine;
	}

	/**
	 * 根据用户引入的模板引擎jar，自动创建对应的模板引擎对象<br>
	 * 推荐创建的引擎单例使用，此方法每次调用会返回新的引擎
	 *
	 * @param config 模板配置，包括编码、模板文件path等信息
	 * @return {@link TemplateEngine}
	 */
	private static TemplateEngine doCreate(TemplateConfig config) {
		final Class<? extends TemplateEngine> customEngineClass = config.getCustomEngine();
		final TemplateEngine engine;
		if(null != customEngineClass){
			engine = ReflectUtil.newInstance(customEngineClass);
		}else{
			engine = ServiceLoaderUtil.loadFirstAvailable(TemplateEngine.class);
		}
		if(null != engine){
			return engine.init(config);
		}

		throw new TemplateException("No template found ! Please add one of template jar to your project !");
	}
}
