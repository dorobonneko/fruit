package moe.mpi;
import android.content.Context;
import android.view.View;
import android.app.Fragment;
import java.io.File;
import javax.net.ssl.SSLSocketFactory;
import moe.mpi.util.SslSocketUtils;
import java.util.HashMap;
import java.util.Map;
import moe.mpi.handle.HandleThread;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;
import android.util.DisplayMetrics;
import android.os.Handler;
import android.os.Looper;
import moe.mpi.decode.ImageDecoder;
import moe.mpi.util.Size;
import moe.mpi.decode.FileDecoder;
import android.graphics.Bitmap;
import moe.mpi.decode.BitmapDecoder;
import android.graphics.drawable.Drawable;
import moe.mpi.decode.DrawableDecoder;

public class Mpi {
	private Context c;
	private DiskCache mDiskCache;
	private Dispatcher mDispatcher;
	private SSLSocketFactory ssf;
	private ThreadPoolExecutor tpe;
	private ThreadPoolExecutor decoderThread;
	private Map<String,HandleThread> requests=new HashMap<>();
	private Map<Class<?>,Decoder<?>> mDecoder=new HashMap<>();
	private BitmapPool mBitmapPool=new BitmapPool(128*1024*1024);
	private Handler handler=new Handler(Looper.getMainLooper());
	private ActiveResource mActiveResource=new ActiveResource();
	public Mpi(Context c){
		this.c=c;
		tpe=new ThreadPoolExecutor(4,32,15,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(300));
		decoderThread=new ThreadPoolExecutor(4,16,15,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(300));
		mDecoder.put(File.class,new FileDecoder(c,this));
		mDecoder.put(Bitmap.class,new BitmapDecoder(c));
		mDecoder.put(Drawable.class,new DrawableDecoder(c));
	}
//	public static Mpi $(Context context){
//		if(mpi==null){
//			synchronized(Mpi.class){
//				if(mpi==null)
//					mpi=new Mpi(context);
//			}
//		} 
//		return mpi;
//	}
//	public static Mpi $(View v){
//		return $(v.getContext());
//	}
//	public static Mpi $(Fragment f){
//		return $(f.getContext());
//	}
	public Context getContext(){
		return c;
	}
	public void clearActive(){
		mActiveResource.clearMemory();
	}
	public void clear(View v){
		if(v==null)return;
		Target t=(Target) v.getTag();
		if(t!=null){
			clear(t);
		}
	}
	public void clear(Target t){
		if(t!=null){
			Request req=t.getRequest();
			if(req!=null)
				req.cancel();
			}
	}
	public Request.Builder load(String url){
		return new Request.Builder(this,url);
	}
	public Request.Builder load(int res){
		return new Request.Builder(this,res);
	}
	public String getUserAgent(){
		return "moe:user-agent";
	}
	public String hashCode(String str){
		return String.valueOf(hash(str.toCharArray()));
	}
	public static int hash(char[] str){
		if(str==null)return 0;
		//return Math.abs(Arrays.hashCode(str));
		char[] value=str;
		int hash=0;
		for (int j = 0; j < value.length; j++) {
			// 黄金比例混合
			hash = Integer.rotateLeft(hash, 5) 
				^ (value[j] * 0x9e3779b9) 
				+ (j << 24);
		}
		// 雪崩效应增强
		hash ^= hash >>> 16;
		hash *= 0x85ebca6b;
		hash ^= hash >>> 13;
		return Math.abs(hash);
	}
	public DiskCache getDiskCache(){
		if(mDiskCache==null)
			mDiskCache=DiskCache.getInstance(new File(c.getExternalCacheDir(),"mpi"),-1);
			return mDiskCache;
	}
	public void setDiskCache(DiskCache dc){
		this.mDiskCache=dc;
	}
	public Dispatcher getDispatcher(){
		if(mDispatcher==null)
			mDispatcher=Dispatcher.getDefault(c);
		return mDispatcher;
	}
	public SSLSocketFactory getSSLSocketFactory(){
		if(ssf==null)
			ssf=SslSocketUtils.getSSLSocketFactory();
			return ssf;
	}
	public ThreadPoolExecutor getThreadPoolExecutor(){
		return tpe;
	}
	public ActiveResource getActiveResource(){
		return mActiveResource;
	}
	
	public <T> Decoder<T> getDecoder(Class<T> type){
		return (Decoder<T>)mDecoder.get(type);
	}
	public ThreadPoolExecutor getDecoderThreadPool(){
		return decoderThread;
	}
	public BitmapPool getBitmapPool(){
		return mBitmapPool;
	}
	public Size getScreenSize()
	{
		DisplayMetrics dm=c.getResources().getDisplayMetrics();
		return new Size(dm.widthPixels,dm.heightPixels);
	}
	public Map<String,HandleThread> requests(){
		return requests;
	}
	public void post(Runnable run){
		handler.post(run);
	}
	public void postDelay(Runnable run,long delay){
		handler.postDelayed(run,delay);
	}
	public void release(){
		for(HandleThread ht:requests().values())
			ht.cancel();
		mActiveResource.clearMemory();
		mBitmapPool.clear();
		
	}
}
