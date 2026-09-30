package moe.fruit.data;

public interface Item{
	public static final int TYPE_CLASS=0,TYPE_HEADER=1,TYPE_POST=2,TYPE_PLAY=3;
	String getTitle();
	int getType();
}
