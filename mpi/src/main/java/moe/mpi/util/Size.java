package moe.mpi.util;

public class Size {
	private int w,h;
	public Size(int w,int h){
		this.w=w;
		this.h=h;
	}
	public int getWidth(){
		return w;
	}
	public int getHeight(){
		return h;
	}

	@Override
	public String toString()
	{
		// TODO: Implement this method
		return getWidth()+"x"+getHeight();
	}
	
}
