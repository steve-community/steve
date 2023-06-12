package net.parkl.ocpp.service.chargepoint;

import jakarta.xml.ws.Response;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ResponseWrapper<T> implements Response<T>{
	private final T element;
	
	public ResponseWrapper(T e) {
		this.element=e;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public T get() {
		return element;
	}

	@Override
	public T get(long timeout, TimeUnit unit) {
		return element;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public Map<String, Object> getContext() {
		return null;
	}

}
