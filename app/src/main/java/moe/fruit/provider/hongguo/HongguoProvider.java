package moe.fruit.provider.hongguo;
import android.content.*;
import moe.fruit.provider.*;
import moe.mpi.Mpi;

public class HongguoProvider implements ProviderFactory.Factory{
	private Context ctx;
	private IndexProvider mIndex;
	private Mpi mpi;
	public HongguoProvider(Context ctx){
		this.ctx=ctx;
	}

@Override
public IndexProvider createIndexProvider()
{
 if(mIndex==null)
	 synchronized(this){
		 if(mIndex==null)
			 mIndex=new HongguoIndex();
	 }
return mIndex;
}

@Override
public Mpi createMpi()
{
	if(mpi==null)
		synchronized(this){
			if(mpi==null)
				mpi=new Mpi(ctx);
		}
	return mpi;
}


	
	
	
}
