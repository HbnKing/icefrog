package com.whaleal.icefrog.core.compiler;

import javax.tools.DiagnosticCollector;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 诊断工具类
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class DiagnosticUtil {

	/**
	 * 获取{@link DiagnosticCollector}收集到的诊断信息，以文本返回
	 *
	 * @param collector {@link DiagnosticCollector}
	 * @return 诊断消息
	 */
	public static String getMessages(DiagnosticCollector<?> collector) {
		final List<?> diagnostics = collector.getDiagnostics();
		return diagnostics.stream().map(String::valueOf)
				.collect(Collectors.joining(System.lineSeparator()));
	}
}
