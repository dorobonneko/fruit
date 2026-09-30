package moe.mpi.handle;
import android.net.Uri;
import moe.mpi.RequestHandler;
import java.util.concurrent.ThreadPoolExecutor;
import javax.security.auth.callback.Callback;
import moe.mpi.Request;

public class FileRequestHandler implements RequestHandler
{
	
	@Override
	public boolean canHandle(Request request)
	{
		String scheme=request.getUrl().getScheme();
		if(scheme!=null)
		switch(scheme){
			case "file":
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
