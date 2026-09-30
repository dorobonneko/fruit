package moe.mpi;
import java.util.concurrent.ThreadPoolExecutor;
import android.net.Uri;

public interface RequestHandler
{
	public abstract boolean canHandle(Request request);
	public abstract boolean onHandle(ThreadPoolExecutor pool,Request request,Callback callback);
	public interface Callback{
		void onError(Throwable e);
		void onProgress(long current,long length);
		void onReady(Uri path);
	}
}
