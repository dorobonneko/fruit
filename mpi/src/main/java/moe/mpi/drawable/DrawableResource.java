package moe.mpi.drawable;
import android.graphics.drawable.Drawable;
import moe.mpi.Resource;
import android.graphics.drawable.BitmapDrawable;
import moe.mpi.BitmapPool;

public class DrawableResource<T extends Drawable> extends Resource<T>
{
	private BitmapPool bp;
	public DrawableResource(String key,BitmapPool bp,T obj){
		super(key,obj);
		this.bp=bp;
	}
@Override
public void release()
{
	Drawable d=acquire();
	if(d instanceof BitmapDrawable){
		bp.recycle(((BitmapDrawable)d).getBitmap());
	}
}
	
}
