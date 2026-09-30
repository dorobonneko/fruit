package moe.mpi;
import android.graphics.drawable.Drawable;
import moe.mpi.util.Size;

public interface Target<Z>
{
	default void placeHolder(Drawable placeHolder){};
	 Request getRequest();
	public abstract void onSuccess(Resource<? extends Z> pd);
	default void error(Throwable e,Drawable d){};
	//default void onCancel(){}
	void onAttach(Request c);
	default void getSize(SizeReady ready){
		ready.onSizeReady(new Size(Integer.MAX_VALUE,Integer.MAX_VALUE));
		
	};
	void removeCallback(SizeReady sr);
	void listener(Listener<? extends Z> l);
	
	public  interface Listener<Z>{
		default  void onSuccess(Z z){};
		default void onError(Throwable e){};
		default void onPlaceHolder(){};
		default void onCancel(){};
	}
}
