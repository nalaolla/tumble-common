package kr.co.tumble.common.context;

/**
 * AsyncRequestContextHolder
 */
public class AsyncRequestContextHolder {

	private static final ThreadLocal<AsyncRequestContext> contextHolder = new ThreadLocal<>();

	public void clearContext() {
		contextHolder.remove();
	}
	
	public static AsyncRequestContext getContext() {
		AsyncRequestContext ctx = contextHolder.get();

        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }

        return ctx;
	}

	public static void setContext(AsyncRequestContext context) {
        contextHolder.set(context);
    }

    public static AsyncRequestContext createEmptyContext() {
        return new AsyncRequestContext();
    }

}
