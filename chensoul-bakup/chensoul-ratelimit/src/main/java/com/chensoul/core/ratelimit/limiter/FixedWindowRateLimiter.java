package com.chensoul.core.ratelimit.limiter;

import com.chensoul.core.ratelimit.RateLimiter;
import lombok.Getter;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class FixedWindowRateLimiter implements RateLimiter {
	private final ConcurrentHashMap<String, AtomicReference<ValueHolder>> counterMap = new ConcurrentHashMap<>();
	private final int limit; // 限流阈值
	private final long windowSizeInMillis; // 时间窗口大小
	private final long cleanupIntervalInMillis = 60000; // 清理过期计数器的时间间隔（1分钟）

	public FixedWindowRateLimiter(int limit, long windowSizeInMillis) {
		this.limit = limit;
		this.windowSizeInMillis = windowSizeInMillis;

		// 启动清理线程
		Thread cleanupThread = new Thread(this::cleanupExpiredCounters);
		cleanupThread.setDaemon(true);
		cleanupThread.start();
	}

	public static void main(String[] args) throws InterruptedException {
		RateLimiter rateLimiter = new FixedWindowRateLimiter(100, 1000); // 1秒内最多100次请求

		ExecutorService executor = Executors.newCachedThreadPool();
		Runnable task = () -> {
			for (int i = 0; i < 200; i++) {
				String key = "user" + ThreadLocalRandom.current().nextInt(10); // 随机生成用户ID
				if (rateLimiter.tryAcquire(key)) {
					System.out.println("Request granted for user: " + key);
				} else {
					System.out.println("Request denied for user: " + key);
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		for (int i = 0; i < 10; i++) {
			executor.submit(task);
		}

		executor.shutdown();
		executor.awaitTermination(60, TimeUnit.SECONDS);
	}

	@Override
	public boolean tryAcquire(String key) {
		AtomicReference<ValueHolder> window = counterMap.computeIfAbsent(key, k -> new AtomicReference<>(new ValueHolder(System.currentTimeMillis())));

		return window.updateAndGet(w -> {
			long currentTime = System.currentTimeMillis();
			if (currentTime - w.getTimestamp() > windowSizeInMillis) {
				// 时间窗口已过期，重置计数器
				return new ValueHolder(currentTime);
			} else {
				// 在有效时间窗口内，增加计数
				if (w.getCount() < limit) {
					return new ValueHolder(w.getTimestamp(), w.getCount() + 1);
				} else {
					return w; // 超过限流阈值，不更新
				}
			}
		}).getCount() < limit;
	}

	private void cleanupExpiredCounters() {
		while (true) {
			try {
				Thread.sleep(cleanupIntervalInMillis);
				// 使用 Iterator 安全地遍历和清理过期的计数器
				counterMap.entrySet().removeIf(entry -> {
					ValueHolder valueHolder = entry.getValue().get();
					return valueHolder != null && (System.currentTimeMillis() - valueHolder.getTimestamp()) > windowSizeInMillis;
				});
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}

	@Getter
	private static class ValueHolder {
		private final long timestamp;
		private final int count;

		public ValueHolder(long timestamp) {
			this.timestamp = timestamp;
			this.count = 1;
		}

		public ValueHolder(long timestamp, int count) {
			this.timestamp = timestamp;
			this.count = count;
		}

	}
}
