package moe.fruit;

import android.app.*;
import android.os.*;
import moe.fruit.provider.*;
import moe.fruit.provider.hongguo.*;
import moe.fruit.fragment.*;
import androidx.core.app.*;
import androidx.fragment.app.*;
import androidx.viewpager2.widget.*;
import com.google.android.material.tabs.*;
import moe.fruit.fragment.adapter.*;
import moe.fruit.fragment.tab.*;
import java.util.*;
import androidx.appcompat.widget.*;

public class IndexActivity extends FragmentActivity implements TabLayout.OnTabSelectedListener {
	private ProviderFactory mProvider;
	private IndexFragment mIndex;
	private ViewPager2 pager;
	private TabLayout layout;
	private IndexPager mIndexPager;
	private List<TabFragment> data;
	private Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProvider = new ProviderFactory(new HongguoProvider(this));
		setContentView(R.layout.index);
		pager = findViewById(R.id.pager);
		layout = findViewById(R.id.tablayout);
		toolbar=findViewById(R.id.toolbar);
		
		pager.setAdapter(mIndexPager = new IndexPager(this, data = new ArrayList<>()));
		data.add(new MainFragment());
		data.add(new CollectionFragment());
		mIndexPager.notifyDataSetChanged();
		pager.setUserInputEnabled(false);
		layout.addOnTabSelectedListener(this);
		layout.addTab(layout.newTab().setText("主页"),true);
		layout.addTab(layout.newTab().setText("追剧"));
		
	}
	public ProviderFactory getFactory(){
		if(mProvider==null)throw new NullPointerException();
		return mProvider;
	}
	@Override
	public void onTabReselected(TabLayout.Tab t) {
		// TODO: Implement this method
		data.get(t.getPosition()).refresh();
	}

	@Override
	public void onTabSelected(TabLayout.Tab t) {
		pager.setCurrentItem(t.getPosition());
		toolbar.setTitle(t.getText());
		data.get(t.getPosition()).onSelected(toolbar);
		
	}

	@Override
	public void onTabUnselected(TabLayout.Tab t) {
		// TODO: Implement this method
	}

}

