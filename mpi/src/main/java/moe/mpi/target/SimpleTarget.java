package moe.mpi.target;
import moe.mpi.Target;
import android.graphics.drawable.Drawable;
import moe.mpi.Resource;
import moe.mpi.Target.Listener;
import moe.mpi.Request;
import moe.mpi.SizeReady;

public abstract class SimpleTarget<Z> implements Target<Z>
	{
		protected Listener<Z> l;
	protected Request req;

	@Override
	public void listener(Target.Listener<? extends Z> l)
	{
		this.l=(Listener<Z>)l;
	}
		@Override
		public void onAttach(Request c)
			{
				this.req=c;
			}

			@Override
			public Request getRequest()
			{
				return req;
			}

	
	@Override
	public void removeCallback(SizeReady sr)
		{
			if(l!=null)l.onCancel();
	}


	@Override
	public void error(Throwable e, Drawable d)
		{
			if(l!=null)l.onError(e);
		}

		@Override
		public void placeHolder(Drawable placeHolder)
		{
			if(l!=null)l.onPlaceHolder();
		}

		@Override
		public void onSuccess(Resource<? extends Z> pd)
			{
				if(l!=null)l.onSuccess(pd.peek());
		}

	
}
