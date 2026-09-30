package moe.mpi;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Resource<T extends Object>
{
	private String key;
	private T image;
	private boolean memory;
	private AtomicInteger count=new AtomicInteger(0);
	private ReentrantLock lock=new ReentrantLock();
	private StringBuilder sb=new StringBuilder();
	public Resource(String key){
		this.key=key;
	}
	public Resource(String key,T bitmap){
		this.key=key;
		this.image=bitmap;
	}
	public Resource(String key, T bitmap,boolean memory)
	{
		this.key = key;
		this.image = bitmap;
		this.memory=memory;
	}
	public String getKey(){
		return key;
		}
	public boolean isMemory(){
		return memory;
	}
	public T peek(){
		return image;
	}
	public T acquire()
	{
		count.incrementAndGet();
		return image;
	}
	public void unAcquire(){
		count.decrementAndGet();
	}
	public void release()
	{
		image=null;
	}
	public int activeCount(){
		return count.get();
	}

	@Override
	public String toString()
	{
		// TODO: Implement this method
		return key+"\n当前持有数量"+activeCount()+"\n"+sb.toString();
	}
	
}
