package com.whaleal.icefrog.cron.demo;

import com.whaleal.icefrog.core.lang.Console;
import com.whaleal.icefrog.core.thread.ThreadUtil;
import com.whaleal.icefrog.cron.CronUtil;
import com.whaleal.icefrog.cron.TaskExecutor;
import com.whaleal.icefrog.cron.listener.TaskListener;
import com.whaleal.icefrog.cron.task.Task;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 定时任务样例
 */
public class CronTest {

	@Test
	@Ignore
	public void customCronTest() {
		CronUtil.schedule("*/2 * * * * *", (Task) () -> Console.log("Task excuted."));

		// 支持秒级别定时任务
		CronUtil.setMatchSecond(true);
		CronUtil.start();

		ThreadUtil.waitForDie();
		Console.log("Exit.");
	}

	@Test
	@Ignore
	public void cronTest() {
		// 支持秒级别定时任务
		CronUtil.setMatchSecond(true);
		CronUtil.getScheduler().setDaemon(false);
		CronUtil.start();

		ThreadUtil.waitForDie();
		CronUtil.stop();
	}

	@Test
	@Ignore
	public void cronWithListenerTest() {
		CronUtil.getScheduler().addListener(new TaskListener() {
			@Override
			public void onStart(TaskExecutor executor) {
				Console.log("Found task:[{}] start!", executor.getCronTask().getId());
			}

			@Override
			public void onSucceeded(TaskExecutor executor) {
				Console.log("Found task:[{}] success!", executor.getCronTask().getId());
			}

			@Override
			public void onFailed(TaskExecutor executor, Throwable exception) {
				Console.error("Found task:[{}] failed!", executor.getCronTask().getId());
			}
		});

		// 支持秒级别定时任务
		CronUtil.setMatchSecond(true);
		CronUtil.start();

		ThreadUtil.waitForDie();
		Console.log("Exit.");
	}

	@Test
	@Ignore
	public void addAndRemoveTest() {
		String id = CronUtil.schedule("*/2 * * * * *", (Runnable) () -> Console.log("task running : 2s"));

		Console.log(id);
		CronUtil.remove(id);

		// 支持秒级别定时任务
		CronUtil.setMatchSecond(true);
		CronUtil.start();
	}
}
