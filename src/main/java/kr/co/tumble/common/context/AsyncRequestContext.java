package kr.co.tumble.common.context;

import java.util.HashMap;
import java.util.Map;

/**
 * AsyncRequestContext
 */
public class AsyncRequestContext {
	
	private Map<String, Object> context;
	
	public AsyncRequestContext() {
		context = new HashMap<>();
	}
	
	public Object getAttribute(String key) {
		return context.get(key);
	}

	public Object setAttribute(String key, String value) {
		return context.put(key, value);
	}

	public Object removeAttribute(String key) {
		return context.remove(key);
	}

	public void clear() {
		context.clear();
	}

}
