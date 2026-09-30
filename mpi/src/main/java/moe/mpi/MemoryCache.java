package moe.mpi;
import android.util.LruCache;

public class MemoryCache extends LruCache<String,Image>
{
	//private Map<String,WeakReference> cache=new HashMap<>();
	private BitmapPool bp;
	public MemoryCache(BitmapPool bp){
		super((int)bp.maxSize);
		this.bp=bp;
	}
	
	@Override
	protected void entryRemoved(boolean evicted, String key, Image oldValue, Image newValue)
	{
		if(evicted&&oldValue!=null&&oldValue!=newValue)
			oldValue.recycle(bp);
	}

	@Override
	protected int sizeOf(String key, Image value)
	{
		return value.getByteCount();
	}

}
