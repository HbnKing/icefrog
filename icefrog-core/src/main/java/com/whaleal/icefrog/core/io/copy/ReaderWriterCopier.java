package com.whaleal.icefrog.core.io.copy;

import com.whaleal.icefrog.core.io.IORuntimeException;
import com.whaleal.icefrog.core.io.IoUtil;
import com.whaleal.icefrog.core.io.StreamProgress;
import com.whaleal.icefrog.core.lang.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * {@link Reader} 向 {@link Writer} 拷贝
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class ReaderWriterCopier extends IoCopier<Reader, Writer> {

	/**
	 * 构造
	 */
	public ReaderWriterCopier() {
		this(IoUtil.DEFAULT_BUFFER_SIZE);
	}

	/**
	 * 构造
	 *
	 * @param bufferSize 缓存大小
	 */
	public ReaderWriterCopier(int bufferSize) {
		this(bufferSize, -1);
	}

	/**
	 * 构造
	 *
	 * @param bufferSize 缓存大小
	 * @param count      拷贝总数
	 */
	public ReaderWriterCopier(int bufferSize, long count) {
		this(bufferSize, count, null);
	}

	/**
	 * 构造
	 *
	 * @param bufferSize 缓存大小
	 * @param count      拷贝总数
	 * @param progress   进度条
	 */
	public ReaderWriterCopier(int bufferSize, long count, StreamProgress progress) {
		super(bufferSize, count, progress);
	}

	@Override
	public long copy(Reader source, Writer target) {
		Preconditions.notNull(source, "InputStream is null !");
		Preconditions.notNull(target, "OutputStream is null !");

		final StreamProgress progress = this.progress;
		if (null != progress) {
			progress.start();
		}
		final long size;
		try {
			size = doCopy(source, target, new char[bufferSize(this.count)], progress);
			target.flush();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}

		if (null != progress) {
			progress.finish();
		}
		return size;
	}

	/**
	 * 执行拷贝，如果限制最大长度，则按照最大长度读取，否则一直读取直到遇到-1
	 *
	 * @param source   {@link InputStream}
	 * @param target   {@link OutputStream}
	 * @param buffer   缓存
	 * @param progress 进度条
	 * @return 拷贝总长度
	 * @throws IOException IO异常
	 */
	private long doCopy(Reader source, Writer target, char[] buffer, StreamProgress progress) throws IOException {
		long numToRead = this.count > 0 ? this.count : Long.MAX_VALUE;
		long total = 0;

		int read;
		while (numToRead > 0) {
			read = source.read(buffer, 0, bufferSize(numToRead));
			if (read < 0) {
				// 提前读取到末尾
				break;
			}
			target.write(buffer, 0, read);

			numToRead -= read;
			total += read;
			if (null != progress) {
				progress.progress(total);
			}
		}

		return total;
	}
}
