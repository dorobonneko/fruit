package moe.mpi;
import java.util.*;
import moe.mpi.handle.*;

import android.content.Context;

public class Dispatcher
{
	private List<RequestHandler> handlers=new ArrayList<>();
	public Dispatcher(){}
	public static Dispatcher getDefault(Context context){
		Dispatcher d=new Dispatcher();
		d.add(new HttpRequestHandler());
		d.add(new ResourceRequestHandler(context));
		d.add(new FileRequestHandler());
		return d;
	}
	public void add(RequestHandler handler){
		handlers.add(0,handler);
	}
	public RequestHandler getHandler(Request request){
		for(RequestHandler h:handlers){
			if(h.canHandle(request))
				return h;
		}
		return null;
	}
	public RequestHandler[] getHandlers(){
		return handlers.toArray(new RequestHandler[handlers.size()]);
	}
}
