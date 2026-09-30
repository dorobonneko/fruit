package moe.mpi.bitmap;
import moe.mpi.Resource;
import android.graphics.drawable.BitmapDrawable;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import moe.mpi.BitmapPool;

public class BitmapResource extends Resource<Drawable>{
	
	private Resources res;
	private Resource<Bitmap> bitmapResource;
	private BitmapPool bp;
	public BitmapResource(Resources res,BitmapPool bp,Resource<Bitmap> bitmapResource ){
		super(bitmapResource.getKey());
		this.res=res;
		this.bp=bp;
		this.bitmapResource=bitmapResource;
	}

	@Override
	public Drawable acquire()
	{
		return new BitmapDrawable(res,bitmapResource.acquire());
	}

	@Override
	public void unAcquire()
	{
		// TODO: Implement this method
		bitmapResource.unAcquire();
	}

	@Override
	public int activeCount()
	{
		// TODO: Implement this method
		return bitmapResource.activeCount();
	}

	@Override
	public void release()
	{
		bp.recycle(bitmapResource.acquire());
	}

	@Override
	public String toString()
	{
		// TODO: Implement this method
		return "bitmap:"+bitmapResource.toString();
	}

	
}
