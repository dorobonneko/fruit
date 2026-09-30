package moe.mpi;

public class Key{
	StringBuilder sb=new StringBuilder();
	private int id;
	public Key(int id){
		this.id=id;
		sb.append(id);
	}
	public void append(String data){
		sb.append(data);
	}

	@Override
	public String toString()
	{
		return sb.toString();
	}
	public String getKey(){
		return String.valueOf(id);
	}
}
