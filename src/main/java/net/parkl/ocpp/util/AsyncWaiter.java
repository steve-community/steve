package net.parkl.ocpp.util;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncWaiter<T> {
	private static final Logger LOGGER=LoggerFactory.getLogger(AsyncWaiter.class);
	
	private long delayMs=100;
	private long timeoutMs;
	private long intervalMs=1000;
	private T value;
	private int cnt;
	
	public AsyncWaiter(long timeoutMs) {
		this.timeoutMs=timeoutMs;
	}
	public T waitFor(Callable<T> callable) {
		Callable<Boolean> condition=new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				cnt++;
				LOGGER.debug("Polling for value: #{}...",cnt);
				value=callable.call();
				return value!=null;
			}
		};
		try {
			Awaitility.with().pollInSameThread().pollDelay(delayMs, TimeUnit.MILLISECONDS).and().pollInterval(intervalMs, TimeUnit.MILLISECONDS).
				atMost(timeoutMs, TimeUnit.MILLISECONDS).await().until(condition);
		} catch (ConditionTimeoutException ex) {
			LOGGER.info("Polling timeout after {} ms",timeoutMs);
		}
		return value;
	}
	public void setDelayMs(long delayMs) {
		this.delayMs = delayMs;
	}
	public void setIntervalMs(long intervalMs) {
		this.intervalMs = intervalMs;
	}
}
