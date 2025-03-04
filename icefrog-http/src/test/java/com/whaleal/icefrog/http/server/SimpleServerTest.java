package com.whaleal.icefrog.http.server;

import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.lang.Console;
import com.whaleal.icefrog.core.net.multipart.UploadFile;
import com.whaleal.icefrog.http.ContentType;
import com.whaleal.icefrog.http.HttpUtil;
import com.whaleal.icefrog.json.JSONUtil;

public class SimpleServerTest {

	public static void main(String[] args) {
		HttpUtil.createServer(8888)
				.addFilter(((req, res, chain) -> {
					Console.log("Filter: " + req.getPath());
					chain.doFilter(req.getHttpExchange());
				}))
				// 设置默认根目录，classpath/html
				.setRoot(FileUtil.file("html"))
				// get数据测试，返回请求的PATH
				.addAction("/get", (request, response) ->
						response.write(request.getURI().toString(), ContentType.TEXT_PLAIN.toString())
				)
				// 返回JSON数据测试
				.addAction("/restTest", (request, response) -> {
					String res = JSONUtil.createObj()
							.set("id", 1)
							.set("method", request.getMethod())
							.set("request", request.getBody())
							.toStringPretty();
					response.write(res, ContentType.JSON.toString());
				})
				// 获取表单数据测试
				// http://localhost:8888/formTest?a=1&a=2&b=3
				.addAction("/formTest", (request, response) ->
						response.write(request.getParams().toString(), ContentType.TEXT_PLAIN.toString())
				)

				// 文件上传测试
				// http://localhost:8888/formForUpload.html
				.addAction("/file", (request, response) -> {
							Console.log("Upload file...");
							Console.log(request.getParams());
							final UploadFile[] files = request.getMultipart().getFiles("file");
							// 传入目录，默认读取HTTP头中的文件名然后创建文件
							for (UploadFile file : files) {
								file.write("d:/test/");
								Console.log("Write file: d:/test/" + file.getFileName());
							}
							response.write(request.getMultipart().getParamMap().toString(), ContentType.TEXT_PLAIN.toString());
						}
				)
				// 测试输出响应内容是否能正常返回Content-Length头信息
				.addAction("test/zeroStr", (req, res)-> {
					res.write("0");
					Console.log("Write 0 OK");
				})
				.start();
	}
}
