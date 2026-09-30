package moe.mpi.handle;
import java.util.concurrent.*;
import moe.mpi.*;

import java.lang.ref.WeakReference;
import java.util.Set;
import android.net.Uri;

public class HandleThread implements Runnable, RequestHandler.Callback {
	private Throwable err;
	private Uri url;
	private Request request;
	private Set<Callback> calls = new CopyOnWriteArraySet<>();
	private boolean success;
	private WeakReference<ThreadPoolExecutor> pool;
	public HandleThread(Request request, ThreadPoolExecutor pool) {
		this.request = request;
		this.pool = new WeakReference<ThreadPoolExecutor>(pool);
		pool.execute(this);
	}
	public void cancel(){
		calls.clear();
	}
	private void onStart() {
		for (final Callback call : calls) {
			call.onStart();
		}
	}
	private void success() {
		synchronized (calls) {
			success = true;
			for (final Callback call : calls) {
				call.onReady(url);
			}
			calls.clear();
			if (request != null)
				request.getMpi().requests().remove(request.getKey());
			request = null;
		}
	}

	public void addCallback(Callback call) {
		synchronized (calls) {
			if (!success)
				calls.add(call);
			else if (url == null)
				call.onError(err);
			else
				call.onReady(url);
		}
	}
	public void removeCallback(Callback call) {
		synchronized (calls) {
			calls.remove(call);
			if (calls.isEmpty())
				if (request != null)
					request.getMpi().requests().remove(request.getKey());
			request = null;
		}
	}
	@Override
	public void run() {
		if (success)
			return;
		onStart();
		Request req = request;
		if (req == null)
			return;
		boolean flag = false;
		RequestHandler[] hs = req.getMpi().getDispatcher().getHandlers();
		for (RequestHandler rh : hs) {
			if (request!=null&&rh.canHandle(req)) {
				flag = true;
				if (rh.onHandle(pool.get(), req, this)) {
					break;
				}
			}
		}
		if (flag)
			onError(new IllegalStateException("没有请求处理器"));

	}

	@Override
	public void onReady(final Uri url) {
		this.url = url;
		success();
	}

	@Override
	public void onError(Throwable e) {
		this.err = e;
		synchronized (calls) {
			success = true;
			for (final Callback call : calls) {
				call.onError(e);
			}
			calls.clear();
			if (request != null)
				request.getMpi().requests().remove(request.getKey());
			request = null;
		}
	}

	@Override
	public void onProgress(long current, long length) {
		synchronized (calls) {
			for (final Callback call : calls) {
				call.onProgress(current, length);
			}
		}
	}

	public interface Callback extends RequestHandler.Callback {
		void onStart();
	}
}

