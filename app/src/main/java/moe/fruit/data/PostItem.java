package moe.fruit.data;

public class PostItem implements Item
{
	private String title,cover,hot,episode;
	private Object tag;
	public PostItem(String title,Object tag){
		this.title=title;
	}

	public PostItem setTitle(String title)
	{
		this.title = title;
		return this;
	}

	
	public PostItem setCover(String cover)
	{
		this.cover = cover;
		return this;
	}

	public String getCover()
	{
		return cover;
	}

	public PostItem setHot(String hot)
	{
		this.hot = hot;
		return this;
	}

	public String getHot()
	{
		return hot;
	}

	public PostItem setEpisode(String episode)
	{
		this.episode = episode;
		return this;
	}

	public String getEpisode()
	{
		return episode;
	}
	
	@Override
	public String getTitle()
	{
		// TODO: Implement this method
		return title;
	}

	@Override
	public int getType()
	{
		// TODO: Implement this method
		return Item.TYPE_POST;
	}


}
