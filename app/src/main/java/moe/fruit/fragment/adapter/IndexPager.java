package moe.fruit.fragment.adapter;
import androidx.viewpager2.adapter.*;
import androidx.fragment.app.*;
import java.util.*;
import moe.fruit.fragment.tab.*;

public class IndexPager extends FragmentStateAdapter
	{
		private List<TabFragment> data;
		public IndexPager(FragmentActivity fa,List<TabFragment> data){
			super(fa);
			this.data=data;
		}
	@Override
	public int getItemCount()
		{
		// TODO: Implement this method
	return data.size();
	}

	@Override
	public Fragment createFragment(int p)
		{
	return (Fragment)data.get(p);
	}


}
