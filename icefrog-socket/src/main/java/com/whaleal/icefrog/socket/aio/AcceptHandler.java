package com.whaleal.icefrog.socket.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.whaleal.icefrog.log.StaticLog;

/**
 * 接入完成回调，单例使用
 *
 * @author Looly
 * @author wh
 *
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {

	@Override
	public void completed(AsynchronousSocketChannel socketChannel, AioServer aioServer) {
		// 继续等待接入（异步）
		aioServer.accept();

		final IoAction<ByteBuffer> ioAction = aioServer.ioAction;
		// 创建Session会话
		final AioSession session = new AioSession(socketChannel, ioAction, aioServer.config);
		// 处理请求接入（同步）
		ioAction.accept(session);

		// 处理读（异步）
		session.read();
	}

	@Override
	public void failed(Throwable exc, AioServer aioServer) {
		StaticLog.error(exc);
	}

}
