package com.asiainfo.ti.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.ErrorHandler;

public abstract class AbstractContainer implements InitializingBean, ApplicationContextAware, BeanNameAware, DisposableBean, SmartLifecycle{
	
	private final static Log logger = LogFactory.getLog(AbstractContainer.class);
	
	private volatile String beanName;

	private volatile boolean autoStartup = true;

	private int phase = Integer.MAX_VALUE;

	private volatile boolean active = false;
	private volatile boolean running = false;

	private final Object lifecycleMonitor = new Object();
	private final Object consumerMonitor = new Object();

	private volatile boolean initialized;
	
	private int startConsumerMinInterval = 10*1000;
	private int stopConsumerMinInterval = 10*1000;
	private long lastprocessorstarted = System.currentTimeMillis();
	private long lastprocessorstopped = System.currentTimeMillis();
	
	private int maxConcurrentprocessors = 10;
	private int concurrentprocessors = 1;
	private int consumerAddInterval = 10;
	public int getConsumerAddInterval() {
		return consumerAddInterval;
	}

	public void setConsumerAddInterval(int consumerAddInterval) {
		this.consumerAddInterval = consumerAddInterval;
	}

	public int getConsumerDelInterval() {
		return consumerDelInterval;
	}

	public void setConsumerDelInterval(int consumerDelInterval) {
		this.consumerDelInterval = consumerDelInterval;
	}

	private int consumerDelInterval  = 10;
	

	public int getStartConsumerMinInterval() {
		return startConsumerMinInterval;
	}

	public void setStartConsumerMinInterval(int startConsumerMinInterval) {
		this.startConsumerMinInterval = startConsumerMinInterval;
	}

	public int getStopConsumerMinInterval() {
		return stopConsumerMinInterval;
	}

	public void setStopConsumerMinInterval(int stopConsumerMinInterval) {
		this.stopConsumerMinInterval = stopConsumerMinInterval;
	}

	public int getMaxConcurrentprocessors() {
		return maxConcurrentprocessors;
	}

	public void setMaxConcurrentprocessors(int maxConcurrentprocessors) {
		this.maxConcurrentprocessors = maxConcurrentprocessors;
	}

	public int getConcurrentprocessors() {
		return concurrentprocessors;
	}

	public void setConcurrentprocessors(int concurrentprocessors) {
		this.concurrentprocessors = concurrentprocessors;
	}

	private volatile ApplicationContext applicationContext;

	private Map<ConsumerRunnable,Boolean> processors ;
	private volatile Executor taskExecutor = new SimpleAsyncTaskExecutor();
	
	private void inite(){
		logger.info("inite message container.");
		synchronized (lifecycleMonitor) {
			doInite();
			initialized = true;
		}
	}
	
	public void start() {
		synchronized (lifecycleMonitor) {
			if(initialized){
				doStart();
				
				active = true;
				running = true;
			}else{
				throw new RuntimeException("Can not start Container without inite it!");
			}
		}
	}

	public void stop() {
		
	}

	public boolean isRunning() {
		synchronized (lifecycleMonitor) {
			return this.running ;
		}
	}

	public int getPhase() {
		synchronized (lifecycleMonitor) {
			return this.phase;
		}
	}

	public boolean isAutoStartup() {
		return this.autoStartup;
	}

	public void stop(Runnable callback) {
		
	}

	public void destroy() throws Exception {
		
	}

	public void setBeanName(String name) {
		this.beanName = name;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	protected abstract MessageConsumer getMessageConsumer();
	
	protected int initializeprocessors() {
		int count = 0;
		synchronized (consumerMonitor) {
			if (this.processors == null) {
				this.processors = new HashMap<ConsumerRunnable, Boolean>(this.concurrentprocessors);
				for (int i = 0; i < this.concurrentprocessors; i++) {
					ConsumerRunnable cr = new ConsumerRunnable(getMessageConsumer());
					processors.put(cr, true);
					count++;
				}
			}
		}
		return count;
	}
	
	private void addAndStartprocessors(int count){
		synchronized (consumerMonitor) {
			for(int i=0;i<count;i++){
				ConsumerRunnable cr = new ConsumerRunnable(getMessageConsumer());
				processors.put(cr, true);
				taskExecutor.execute(cr);
			}
		}
	}
	
	private void considerAddingAConsumer() {
		synchronized(consumerMonitor) {
			if (this.processors != null && this.processors.size() < this.maxConcurrentprocessors) {
				long now = System.currentTimeMillis();
				if (this.lastprocessorstarted + startConsumerMinInterval < now) {
					this.addAndStartprocessors(1);
					this.lastprocessorstarted = now;
				}
			}
		}
	}

	private void considerStoppingAConsumer(ConsumerRunnable consumer) {
		synchronized (consumerMonitor) {
			if (this.processors != null && this.processors.size() > concurrentprocessors) {
				long now = System.currentTimeMillis();
				if (this.lastprocessorstopped + this.stopConsumerMinInterval < now) {
					consumer.stop();
					this.processors.put(consumer, false);
					if (logger.isDebugEnabled()) {
						logger.debug("Idle consumer terminating: " + consumer);
					}
					this.lastprocessorstopped = now;
				}
			}
		}
	}

	/**
	 * inite the container 
	 */
	public void afterPropertiesSet() throws Exception {
		logger.info("after properties set method.");
		inite();
		if(autoStartup){
			start();
		}
	}
	
	protected void doInite(){
		initializeprocessors();
	}
	
	protected void doStart(){
		for(ConsumerRunnable cr:processors.keySet()){
			taskExecutor.execute(cr);
		}
	}
	
 class ConsumerRunnable implements Runnable {
	 
	 private final Log logger = LogFactory.getLog(ConsumerRunnable.class);

		private MessageConsumer consumer;
		
		private volatile boolean active = true; 
		
		private int positiveCycleCount = 0;
		private int negativeCycleCount = 0;
		
		public ConsumerRunnable(MessageConsumer consumer){
			this.consumer = consumer;
		}
		
		public boolean isActive(){
			if(isRunning()&&active){
				return true;
			}else{
				return false;
			}
		}
		
		public void disable(){
			this.active = false;
		}
		
		
		
		public void run() {
			logger.info("start consumer..");
			while(isActive()){
				try{
					boolean getf = this.consumer.getAndExecute();
					if(getf){
						negativeCycleCount = 0;
						if(++positiveCycleCount>=AbstractContainer.this.consumerAddInterval){
							considerAddingAConsumer();
							//positiveCycleCount = 0;
						}
					}else{
						positiveCycleCount = 0;
						if(++negativeCycleCount>=AbstractContainer.this.consumerDelInterval){
							considerStoppingAConsumer(this);
							//negativeCycleCount = 0;
						}
					}
				}catch(InterruptedException e){
					synchronized (AbstractContainer.this.consumerMonitor) {
						
					}
				}catch(Throwable t){
					considerStoppingAConsumer(this);
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					logger.error("Consumer Runnable error!", t);
				}
			}
			
			processors.remove(this);
		}
		
		public void stop(){
			try {
				consumer.close();
			} catch (IOException e) {
			}
			active = false;
			Thread.currentThread().interrupt();
		}
		
	}
 
	
}
