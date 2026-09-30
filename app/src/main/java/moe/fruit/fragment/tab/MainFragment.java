package moe.fruit.fragment.tab;
import android.os.*;
import android.view.*;
import androidx.fragment.app.*;
import moe.fruit.*;
import androidx.appcompat.widget.*;
import moe.fruit.provider.*;
import com.google.android.material.tabs.*;
import androidx.viewpager2.widget.*;
import moe.fruit.data.*;
import java.util.concurrent.*;
import androidx.core.os.*;
import java.util.*;
import androidx.recyclerview.widget.*;
import moe.fruit.model.*;
import java.security.*;
import java.util.function.*;
import moe.fruit.util.*;
import android.widget.Toast;

public class MainFragment extends Fragment
		implements
			TabFragment,
			Toolbar.OnMenuItemClickListener,
			TabLayout.OnTabSelectedListener {

	private ProviderFactory factory;
	private TabLayout tabLayout;
	private RecyclerView recyclerView;
	private IndexProvider provider;
	private List<PostItem> data;
	private PostAdapter postAdapter;
	@Override
	public String getTitle() {
		// TODO: Implement this method
		return "主页";
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		factory = ((IndexActivity) getActivity()).getFactory();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		provider = factory.getIndex();
		for (ClassItem item : provider.getHeader()) {
			tabLayout.addTab(tabLayout.newTab().setText(item.getTitle()).setTag(item));
		}
		tabLayout.getTabAt(0).select();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO: Implement this method
		return inflater.inflate(R.layout.main, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		tabLayout = view.findViewById(R.id.head);
		recyclerView = view.findViewById(R.id.body);
		tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
		tabLayout.setOnTabSelectedListener(this);
		recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
		recyclerView.setAdapter(postAdapter = new PostAdapter(factory.getMpi(),data = new ArrayList<>()));
	}

	@Override
	public void refresh() {
		data.clear();
		postAdapter.notifyDataSetChanged();
		int index = tabLayout.getSelectedTabPosition();
		if (index >= 0) {
			TabLayout.Tab tab = tabLayout.getTabAt(index);
			provider.reset();
			Async.async(() -> {
				return (List<PostItem>) provider.getData((ClassItem) tab.getTag());
			}, this::post).then(t -> {
				int start = data.size();
				data.addAll(t);
				postAdapter.notifyItemRangeInserted(start, data.size() - start);
			},e->{
				Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
			}, recyclerView::post);
		}
	}
	void post(Runnable run){
		new Thread(run).start();
	}
	@Override
	public void onSelected(Toolbar toolbar) {
		Menu menu = toolbar.getMenu();
		menu.clear();
		menu.add("搜索");
		toolbar.setOnMenuItemClickListener(this);
	}

	@Override
	public boolean onMenuItemClick(MenuItem menuItem) {
		// TODO: Implement this method
		return true;
	}

	@Override
	public void onTabSelected(TabLayout.Tab t) {
		refresh();
	}

	@Override
	public void onTabReselected(TabLayout.Tab t) {
		// TODO: Implement this method
	}

	@Override
	public void onTabUnselected(TabLayout.Tab t) {
		// TODO: Implement this method
	}

}

