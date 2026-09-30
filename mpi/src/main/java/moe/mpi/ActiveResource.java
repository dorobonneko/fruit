package moe.mpi;
import moe.mpi.util.LruCache;
import java.util.Map;
import java.util.HashMap;
public class ActiveResource{
	LruCache<String,Resource> sCache;
	Map<String,Resource> cache=new HashMap<>();
	public ActiveResource() {
		sCache=new LruMemoryCache();
	}
	public void put(Resource res){
		if(res!=null)
		cache.put(res.getKey(),res);
	}
	public void remove(Resource res){
		res.unAcquire();
		if(res.activeCount()<=0){
			cache.remove(res.getKey());
			sCache.put(res.getKey(),res);
		}
	}
	public Resource get(String key){
		if(key==null)
			return null;
		Resource res=cache.get(key);
		if(res==null) {
			res=sCache.remove(key);
		if(res!=null)
			cache.put(key,res);
		}
		return res;
	}
	public void clearMemory(){
		
	}

@Override
public String toString()

{
return sCache.getCurrentSize()+cache.toString();
}
	
	class LruMemoryCache extends LruCache<String,Resource>{
		public LruMemoryCache(){
			super(128);
		}
		@Override
		protected boolean onItemEvicted(String key, Resource item, Resource newItem)
		{
			if(item.activeCount()==0){
				item.release();
				return true;
			}
			return false;
		}
		
	}
}

