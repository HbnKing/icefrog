package com.whaleal.icefrog.socket.aio;

import java.nio.channels.CompletionHandler;

import com.whaleal.icefrog.socket.SocketRuntimeException;

/**
 * 数据读取完成回调，调用Session中相应方法处理消息，单例使用
 *
 * @author Looly
 * @author wh
 *
 */
public class ReadHandler implements CompletionHandler<Integer, AioSession> {

	@Override
	public void completed(Integer result, AioSession session) {
		session.callbackRead();
	}

	@Override
	public void failed(Throwable exc, AioSession session) {
		throw new SocketRuntimeException(exc);
	}

}
