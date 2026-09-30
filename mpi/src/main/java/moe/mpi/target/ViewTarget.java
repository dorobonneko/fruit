package moe.mpi.target;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import java.lang.ref.WeakReference;
import android.graphics.Rect;
import java.io.File;
import java.lang.ref.SoftReference;
import moe.mpi.Target;
import moe.mpi.Request;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import moe.mpi.util.Size;
import moe.mpi.SizeReady;
import android.view.View.OnLayoutChangeListener;

public abstract class ViewTarget<V extends View>  extends SimpleTarget implements View.OnLayoutChangeListener,View.OnAttachStateChangeListener,ViewTreeObserver.OnPreDrawListener,Runnable
{
	private  Request content;
	private SoftReference<V> view;
	private Size mSize;
	private SizeReady sr;
	public ViewTarget(View view)
	{
		this.view =new SoftReference(view);
		//view.addOnAttachStateChangeListener(this);
	}

	@Override
	public void run()
	{
		View v=getView();
		int width=v.getWidth()-v.getPaddingLeft()-v.getPaddingRight();
		int height=v.getHeight()-v.getPaddingTop()-v.getPaddingBottom();
		mSize=new Size(width,height);
		if(sr!=null)
		sr.onSizeReady(mSize);
	}

	@Override
	public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
	{
		run();
		getView().removeOnLayoutChangeListener(this);
	}



	@Override
	public void onViewAttachedToWindow(View v)
	{
		// TODO: Implement this method
		Request req=getRequest();
		if(req!=null){
			if(req.isCancel())
				req.begin();
			}
	}

	@Override
	public void onViewDetachedFromWindow(View v)
	{
		// TODO: Implement this method
		Request req=getRequest();
		if(req!=null){
			req.cancel();
			}
	}

	@Override
	public boolean onPreDraw()
	{
		View v=getView();
		int width=v.getWidth()-v.getPaddingLeft()-v.getPaddingRight();
		int height=v.getHeight()-v.getPaddingTop()-v.getPaddingBottom();
		if(width>0&&height>0){
		mSize=new Size(width,height);
		v.getViewTreeObserver().removeOnPreDrawListener(this);
		}
		return true;
	}



	@Override
	public Request getRequest()
	{
		// TODO: Implement this method
		return content;
	}

	@Override
	public void onAttach(Request c)
	{
		content=c;
		View v=getView();
		//v.addOnAttachStateChangeListener(this);
		if(mSize==null)
		v.getViewTreeObserver().addOnPreDrawListener(this);
		
	}

	
	public V getView()
	{
	return view.get();
	}

	@Override
	public void getSize(final SizeReady ready)
		{
			sr=ready;
			if (mSize==null)
			{
				getView().addOnLayoutChangeListener(this);
				/*getView().removeCallbacks(this);
				getView().post(this);*/
			}
			else
			{
				ready.onSizeReady(mSize);
			}
		}

		@Override
		public void removeCallback(SizeReady sr)
		{
			sr=null;
			getView().getViewTreeObserver().removeOnPreDrawListener(this);
			
		}



}
