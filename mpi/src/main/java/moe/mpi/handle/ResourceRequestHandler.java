package moe.mpi.handle;
import moe.mpi.*;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import java.lang.ref.WeakReference;
import java.util.concurrent.ThreadPoolExecutor;
import javax.security.auth.callback.Callback;

public class ResourceRequestHandler implements RequestHandler
{
	private WeakReference<Resources> res;
	public ResourceRequestHandler(Context context){
		res=new WeakReference<>(context.getResources());
	}
	@Override
	public boolean canHandle(Request request)
	{
		String scheme=request.getUrl().getScheme();
		if(scheme!=null)
		switch(scheme){
			case "drawable":
				return true;
		}
		return false;
	}

	@Override
	public boolean onHandle(ThreadPoolExecutor pool,final Request request,final Callback call)
	{
		call.onReady(request.getUrl());
		return true;
	}
	
}
