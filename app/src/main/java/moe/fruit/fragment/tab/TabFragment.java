package moe.fruit.fragment.tab;
import androidx.appcompat.widget.*;

public interface TabFragment{
	String getTitle();
	void refresh();
	void onSelected(Toolbar toolbar);
}
