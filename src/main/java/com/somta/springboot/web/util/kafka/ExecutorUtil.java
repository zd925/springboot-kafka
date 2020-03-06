package com.somta.springboot.web.util.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class ExecutorUtil {

	private static Logger LOGGER = LogManager.getLogger(ExecutorUtil.class);

	/**
	 * 当该线程池里提交的任务时,如果当前线程池里的任务都在处理任务中。当前提交任务会阻塞
	 * 	 *
	 * @param nThreads
	 * @return
	 */
	public static ExecutorService newBlockThreadPool(int nThreads) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
				new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
						if (!executor.isShutdown()) {
							try {
								executor.getQueue().put(r);
							} catch (InterruptedException e) {
								LOGGER.error("", e);
							}
						}
					}
				});
	}

	public static ExecutorService newBlockThreadPool(int nThreads, String name) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
				new NamedThreadFactory(name), new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				if (!executor.isShutdown()) {
					try {
						executor.getQueue().put(r);
					} catch (InterruptedException e) {
						LOGGER.error("", e);
					}
				}
			}
		});
	}

	public static class NamedThreadFactory implements ThreadFactory {

		private static final AtomicInteger poolNumber = new AtomicInteger(1);
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;

		public NamedThreadFactory(String name) {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = name + '-' + poolNumber.getAndIncrement() + "-thread-";
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon()) {
				t.setDaemon(false);
			}
			if (t.getPriority() != Thread.NORM_PRIORITY) {
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}
	}
}
