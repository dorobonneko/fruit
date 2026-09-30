package moe.mpi.internel;
import moe.mpi.Resource;

public class SimpleResource<T> extends Resource<T>{
	public SimpleResource(String key,T obj){
		super(key,obj);
	}
}
