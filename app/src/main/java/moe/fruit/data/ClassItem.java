package moe.fruit.data;

public class ClassItem implements Item
{
	private String title,category;
	public ClassItem(String title,String category){
		this.title=title;
		this.category=category;
	}
	@Override
	public int getType()
	{
		// TODO: Implement this method
		return Item.TYPE_CLASS;
	}

	@Override
	public String getTitle()
	{
		// TODO: Implement this method
		return title;
	}

	public String getCategory(){
		return category;
	}

}
