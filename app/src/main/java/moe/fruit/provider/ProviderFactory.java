package moe.fruit.provider;
import moe.mpi.Mpi;

public class ProviderFactory{
	private Factory mFactory;
	
	public ProviderFactory(Factory factory){
		this.mFactory=factory;
	}
	public IndexProvider getIndex(){
		return mFactory.createIndexProvider();
	}
	public Mpi getMpi(){
		return mFactory.createMpi();
	}
	public interface Factory{
		IndexProvider createIndexProvider();
		Mpi createMpi();
	}
}
