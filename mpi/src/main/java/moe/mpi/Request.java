package moe.mpi;
import java.util.Map;
import java.util.HashMap;
import android.net.Uri;
import java.util.concurrent.atomic.AtomicBoolean;
import android.text.TextUtils;
import moe.mpi.handle.HandleThread;
import android.graphics.drawable.Drawable;
import java.io.File;
import moe.mpi.drawable.MpiDrawable;
import android.view.View;
import moe.mpi.target.ViewTarget;
import moe.mpi.target.ImageViewTarget;
import android.widget.ImageView;
import moe.mpi.util.Size;
import android.widget.Toast;
import moe.mpi.drawable.TextDrawable;
import android.util.Log;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import android.graphics.Bitmap;
import java.util.Objects;

public class Request {
	private Builder b;
	private boolean sizeReady;
	private HandleThread.Callback callback = new HandleCallback();
	private Size mSize;
	private Target<?> target;
	private Uri resource;
	private Resource<?> mResource;
	private HandleThread ht;
	private SizeReady sr;
	private StringBuilder sb=new StringBuilder(String.valueOf(hashCode())).append("\n");
	private Request(Builder b) {
		this.b = b;
	}
	

	Size getSize() {
		return mSize;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Request)
			return Objects.equals(getImageKey(),((Request) obj).getImageKey());
		return this == obj;
	}

	public Uri getUrl() {
		return b.url;
	}
	public String getKey() {
		Key k=b.getKey();
		if(k==null)
			return null;
		return k.getKey();
	}
	public String getImageKey() {
		Key k=b.getKey();
		if(k==null)
			return null;
		return k.toString();
	}
	public Map<String, String> getHeader() {
		return b.getHeader();
	}
	public boolean isCancel() {
		return b.isCancel();
	}
	public void begin() {
		if (isCancel()&&target!=null) {
			b.cancel(false);
			if(mResource==null)
			target.getSize(sr==null?sr=new SReady():sr);
			else
				onSuccess(mResource);
		}
	}
	public void cancel() {
		if (isCancel())
			return;
			sb.append("取消任务\n");
		b.cancel(true);
		target.removeCallback(sr);
		if (ht != null)
			ht.removeCallback(callback);
		ht = null;
		if (mResource != null){
			sb.append("释放资源:").append(mResource.activeCount()).append("\n");
			getMpi().getActiveResource().remove(mResource);
			sb.append(mResource.toString());
			}
		mResource = null;
	}
	public String getLog(){
		StringBuilder sb=new StringBuilder(this.sb);
		if(mResource!=null)
			sb.append(mResource.toString());
		return sb.toString();
	}
	public Mpi getMpi() {
		return b.p;
	}

	
	
	private ImageViewTarget into(ImageView v) {
		ImageViewTarget t = (ImageViewTarget) v.getTag();
		if (t == null)
			v.setTag(t = new ImageViewTarget(v));
		return (ImageViewTarget)into(t);
	}
	private Target<?> into(Target<?> t) {
		if (t.getRequest() != null) {
			if (t.getRequest().equals(this))
				return t;
			//取消旧任务绑定关系
			//sb.append("取消绑定\n");
			t.getRequest().cancel();
		}
		t.onAttach(this);
		target = t;
		sb.append(b.url);
		sb.append("获取尺寸\n");
		t.getSize(sr=new SReady());
		//从缓存加载图片
		//从网络加载图片
		return t;
	}
	private void loadFromResource() {
		if(isCancel())return;
		if (resource == null){
			onError(new NullPointerException("没有资源"));
			return;
			}
		final Size size = getSize();
		if(size==null)
			throw new NullPointerException("目标尺寸未初始化");
		getMpi().getDecoderThreadPool().execute(new Runnable() {
			public void run() {
				Decoder<?> d = getMpi().getDecoder(b.outputType);
				if (d != null) {
					Resource<?> result = d.decode(getMpi().getBitmapPool(), resource, getImageKey(), b.origin, size.getWidth(),
							size.getHeight());
							getMpi().getActiveResource().put(result);
							if(isCancel())return;
					if (result != null)
						onSuccess(result);
					else
						onError(new IllegalStateException("解码失败"));
				} else {
					onError(new IllegalArgumentException("不存在的类型" + b.outputType));
				}

			}
		});
	}
	private void loadFromMemory() {
		if (isCancel())
			return;
		if (mResource != null)
			throw new IllegalStateException("资源已加载，无法再次加载");
		//从内存中加载图片
		if(!sizeReady)
			throw new NullPointerException("目标尺寸未初始化");
		sb.append("从内存获取\n");
		ActiveResource ar = getMpi().getActiveResource();
		Resource res = ar.get(getImageKey());
		if (res == null) {
			loadFromCache();
		} else {
			onSuccess(res);
		}
	}
	private void onError(final Throwable e) {
		sb.append(e.toString());
		if (!isCancel()) {
			getMpi().post(new Runnable(){
				public void run() {
				Target t=target;
				if(t==null)return;
				if (b.error) {
					t.error(e, new TextDrawable(Log.getStackTraceString(e)));
				} else {
					t.error(e, b.errorHolder);
				}
			}
			});
		}
	}
	private <T> void onSuccess(final Resource<T> res) {
		if (isCancel())
			return;
			sb.append("已获取到资源\n");
		mResource = res;
		getMpi().post(new Runnable(){
			public void run(){
				Target<T> t=(Target<T>) target;
				if(t!=null)
				t.onSuccess(res);
				}
			});
	}
	private void loadFromCache() {
		if(isCancel())return;
		target.placeHolder(b.placeHolder);
		String key=getKey();
		if(key==null)
			return;
		File cache = getMpi().getDiskCache().getCache(getKey());
		if (cache == null) {
			onError(new IllegalArgumentException("资源不存在"));
			return;
		}
		sb.append("从缓存获取\n");
		
		if (cache.exists()) {
			resource = Uri.fromFile(cache);
			loadFromResource();
		} else {
			//从网络加载
			sb.append("从网络加载\n");
			ht = getMpi().requests().get(getKey());
			if (ht == null) {
				ht = new HandleThread(this, getMpi().getThreadPoolExecutor());
				getMpi().requests().put(getKey(), ht);
			}
			ht.addCallback(callback);
		}
	}
	class HandleCallback implements HandleThread.Callback {

		@Override
		public void onStart() {
		}

		@Override
		public void onProgress(long current, long length) {
			// TODO: Implement this method
		}

		@Override
		public void onError(Throwable e) {
			Request.this.onError(e);
			ht = null;
		}

		@Override
		public void onReady(Uri url) {
			ht = null;
			resource = url;
			getMpi().requests().remove(getKey());
			if (url == null) {
				onError(new RuntimeException("网络加载失败"));
				return;
			}
			loadFromResource();
		}

	}
	class SReady implements SizeReady{
		@Override
		public void onSizeReady(Size mSize) {
			if (mSize != null && (mSize.getWidth() > 0 || mSize.getHeight() > 0)) {
				Request.this.mSize = mSize;
				sizeReady = true;
				if(b.getKey()!=null)
					b.getKey().append(mSize.toString());
					else
				cancel();
			}
			if(sizeReady&&!isCancel()){
				sb.append("获取到尺寸\n");
				loadFromMemory();
			}
		}
	}
	public static class Builder {
		private Drawable placeHolder, errorHolder;
		private Mpi p;
		private Uri url;
		private String location;
		private Map<String, String> header = new HashMap<>();
		private String body;
		private NetListener l;
		private boolean origin,error;
		private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_CENTER;
		private Class<?> outputType = Drawable.class;
		private Key key;
		private AtomicBoolean cancel = new AtomicBoolean();
		public Builder(Mpi p, String url) {
			this.p = p;
			if (!TextUtils.isEmpty(url)) {
				this.url = Uri.parse(url.startsWith("/") ? "file://" + url : url);
			}
			userAgent(p.getUserAgent());
		}
		public Builder(Mpi p, int res) {
			this.p = p;
			this.url = Uri.fromParts("drawable", String.valueOf(res), null);
		}
		public Builder placeHolder(Drawable placeHolder) {
			this.placeHolder = placeHolder;
			return this;
		}
		public Builder showError() {
			error = true;
			return this;
		}
		public Builder error(Drawable errorHolder) {
			this.errorHolder = errorHolder;
			return this;
		}
		private boolean isCancel() {
			return cancel.get();
		}
		private void cancel(boolean cancel) {
			this.cancel.set(cancel);
		}

		public Builder listener(NetListener l) {
			this.l = l;
			return this;
		}
		private NetListener getListener() {
			return l;
		}
		public Builder header(String... header) {
			for (int i = 0; i < header.length; i += 2) {
				this.header.put(header[i], header[i + 1]);
			}
			return this;
		}
		public Builder header(Map<String, String> header) {
			if (header != null)
				this.header.putAll(header);
			return this;
		}
		private Key getKey() {
			if (url == null)
				return null;
			if (key == null){
				key = new Key(url.hashCode());
				key.append(String.valueOf(origin));
				key.append(scaleType.toString());
				key.append(outputType.toString());
				}
			return key;
		}
		private Map<String, String> getHeader() {
			return header;
		}
		private String getBody() {
			return body;
		}
		private Mpi getMpi() {
			return p;
		}
		public Builder userAgent(String useragent) {
			if (useragent == null)
				header.remove("UserAgent");
			else
				header.put("UserAgent", useragent);
			return this;
		}
		public Builder referer(String referer) {
			if (referer == null)
				header.remove("Referer");
			else
				header.put("Referer", referer);
			return this;
		}
		public Builder requestBody(String body) {
			this.body = body;
			return this;
		}
		public Builder asBitmap() {
			this.outputType = Bitmap.class;
			return this;
		}
		public Builder asFile() {
			this.outputType = File.class;
			this.origin=true;
			return this;
		}
		public Builder originImage() {
			this.origin = true;
			return this;
		}
		public Builder scaleType(ImageView.ScaleType type) {
			this.scaleType = type;
			return this;
		}
		public Target<?> into(ImageView view) {
			Request r= new Request(this);
			return r.into(view);
		}
		public Target<?> into(Target<?> view) {
			Request r= new Request(this);
			return r.into(view);
		}
	}

}

