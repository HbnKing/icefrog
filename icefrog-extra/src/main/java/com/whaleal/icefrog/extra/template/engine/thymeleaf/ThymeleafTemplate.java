package com.whaleal.icefrog.extra.template.engine.thymeleaf;

import com.whaleal.icefrog.core.convert.Convert;
import com.whaleal.icefrog.core.io.IoUtil;
import com.whaleal.icefrog.core.lang.TypeReference;
import com.whaleal.icefrog.core.util.CharsetUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;
import com.whaleal.icefrog.extra.template.AbstractTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

/**
 * Thymeleaf模板实现
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class ThymeleafTemplate extends AbstractTemplate implements Serializable {
	private static final long serialVersionUID = 781284916568562509L;

	private final TemplateEngine engine;
	private final String template;
	private final Charset charset;

	/**
	 * 包装Thymeleaf模板
	 *
	 * @param engine Thymeleaf的模板引擎对象 {@link TemplateEngine}
	 * @param template 模板路径或模板内容
	 * @param charset 编码
	 * @return {@link ThymeleafTemplate}
	 */
	public static ThymeleafTemplate wrap(TemplateEngine engine, String template, Charset charset) {
		return (null == engine) ? null : new ThymeleafTemplate(engine, template, charset);
	}

	/**
	 * 构造
	 *
	 * @param engine Thymeleaf的模板对象 {@link TemplateEngine}
	 * @param template 模板路径或模板内容
	 * @param charset 编码
	 */
	public ThymeleafTemplate(TemplateEngine engine, String template, Charset charset) {
		this.engine = engine;
		this.template = template;
		this.charset = ObjectUtil.defaultIfNull(charset, CharsetUtil.CHARSET_UTF_8);
	}

	@Override
	public void render(Map<?, ?> bindingMap, Writer writer) {
		final Map<String, Object> map = Convert.convert(new TypeReference<Map<String, Object>>() {}, bindingMap);
		final Context context = new Context(Locale.getDefault(), map);
		this.engine.process(this.template, context, writer);
	}

	@Override
	public void render(Map<?, ?> bindingMap, OutputStream out) {
		render(bindingMap, IoUtil.getWriter(out, this.charset));
	}

}
