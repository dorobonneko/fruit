package moe.fruit.fragment.tab;
import androidx.fragment.app.*;
import androidx.appcompat.widget.*;

public class CollectionFragment extends Fragment implements TabFragment
{

	@Override
	public String getTitle()
	{
		// TODO: Implement this method
		return "追剧";
	}

	@Override
	public void refresh()
	{
	// TODO: Implement this method
	}

	@Override
	public void onSelected(Toolbar toolbar)
		{
	toolbar.getMenu().clear();
	}




}
