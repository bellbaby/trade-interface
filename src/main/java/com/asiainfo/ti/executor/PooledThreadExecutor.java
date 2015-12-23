package com.asiainfo.ti.executor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PooledThreadExecutor extends ThreadPoolExecutor{

	public PooledThreadExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
}
