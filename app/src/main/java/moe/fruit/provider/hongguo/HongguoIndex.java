package moe.fruit.provider.hongguo;
import moe.fruit.provider.*;
import java.util.*;
import moe.fruit.data.*;
import okhttp3.*;
import java.io.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.json.*;
import org.jsoup.select.Elements;
import java.util.concurrent.atomic.AtomicInteger;
public class HongguoIndex implements IndexProvider {
	private volatile AtomicInteger page = new AtomicInteger(1);
	@Override
	public List<ClassItem> getHeader() {
		List<ClassItem> data = new ArrayList<>();
		data.add(new ClassItem("热门", null));
		data.add(new ClassItem("真人剧", "real-drama"));
		data.add(new ClassItem("漫剧", "comic-drama"));
		data.add(new ClassItem("AI剧", "ai-drama"));
		data.add(new ClassItem("动漫", "comic"));
		return data;
	}

	@Override
	public List<PostItem> getData(ClassItem item) {
		// TODO: Implement this method
		String category = item.getCategory();
		if (category == null) {
			return getHotRank();
		}
		
		return getClass(category);
	}
	private List<PostItem> getClass(String category){
		Request req=new Request.Builder().url("https://hongguoduanju.com/category/"+category+"?pgae="+getPage()).build();
		try{
			Response res = getOkhttp().newCall(req).execute();
			Document doc=Jsoup.parse(res.body().byteStream(),"utf-8",req.url().toString());
			Elements lis=doc.select("div[class^='pc-card-container']>a");
			List<PostItem> data=new ArrayList<>();

			for( int i = 0; i < lis.size(); i++) {
				Element e=lis.get(i);
				String title=e.selectFirst("p[class^='pc-title']").ownText();
				String cover=e.selectFirst("img").absUrl("src");
				//String hot=e.selectFirst("p[class^='pc-metrics']>span").ownText();
				String episode=e.selectFirst("p[class^='pc-episode']").ownText();
				data.add(new PostItem(title,null).setCover(cover).setEpisode(episode));

			}
			page.incrementAndGet();
			return data;
		}catch(IOException e){
			
		}
		return Collections.emptyList();
	}
	private List<PostItem> getHotRank() {
		Request req = new Request.Builder().url("https://hongguoduanju.com/rank/hot-drama?page=" + getPage()).build();
		try {
			Response res = getOkhttp().newCall(req).execute();
			Document doc=Jsoup.parse(res.body().byteStream(),"utf-8",req.url().toString());
			Elements lis=doc.select("main>ol>li>article");
			List<PostItem> data=new ArrayList<>();
			
			for( int i = 0; i < lis.size(); i++) {
				Element e=lis.get(i);
				String title=e.selectFirst("h2").ownText();
				String cover=e.selectFirst("img").absUrl("src");
				String hot=e.selectFirst("p[class^='pc-metrics']>span").ownText();
				data.add(new PostItem(title,null).setCover(cover).setHot(hot));
				
			}
			page.incrementAndGet();
			return data;
			/*String args=doc.selectFirst("script[data-fn-name]").attr("data-fn-args");
			try
			{
				
				String s=new JSONArray(args).getJSONArray(1).getJSONObject(0).getJSONArray("routerDataFnArgs").getString(0);
				JSONArray d=new JSONObject(s).getJSONArray("rankList");
				
				for( int i = 0; i < d.length(); i++) {
					JSONObject item=d.getJSONObject(i);
					JSONArray episodes=item.getJSONArray("episodeVids");
					data.add(new PostItem(item.getString("title"),item).setCover(item.getString("cover")).setEpisode(String.format("共 %d 集",episodes.length())).setHot(item.getString("heatText")));
				}
				return data;
				//throw new NullPointerException(s);
				
			}
			catch (JSONException e)
			{
				throw new IllegalArgumentException(e);
			}*/
			//.getJSONArray("rankList");
				//List<PostItem> data=new ArrayList<>();
			/*for( int i = 0; i < ja.length(); i++) {
				JSONObject jo=ja.getJSONObject(i);
				data.add(new PostItem(jo.getString("title")));
							}*/
			//return data;
			//throw new IllegalArgumentException(hot.toString());
		} catch (IOException e) {
			//throw new IllegalArgumentException(e);
		}
		return Collections.emptyList();
	}
	@Override
	public int getPage() {
		// TODO: Implement this method
		return page.get();
	}

	@Override
	public void reset() {
		page.set(1);
	}

}

