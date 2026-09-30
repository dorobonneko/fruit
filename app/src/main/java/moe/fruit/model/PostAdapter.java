package moe.fruit.model;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import moe.fruit.R;
import moe.fruit.data.PostItem;
import android.widget.ImageView;
import moe.mpi.Mpi;
import android.view.ViewOutlineProvider;
import android.graphics.Outline;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
	private List<PostItem> data;
	private Mpi mpi;
	public PostAdapter(Mpi mpi, List<PostItem> data) {
		this.mpi = mpi;
		this.data = data;
	}
	@Override
	public int getItemCount() {
		// TODO: Implement this method
		return data.size();
	}

	@Override
	public void onViewRecycled(PostAdapter.ViewHolder holder) {
		// TODO: Implement this method
		super.onViewRecycled(holder);
		mpi.clear(holder.cover);
	}

	@Override
	public void onBindViewHolder(ViewHolder vH, int p) {
		PostItem item = data.get(vH.getAbsoluteAdapterPosition());
		mpi.load(item.getCover()).into(vH.cover);
		vH.title.setText(item.getTitle());
		vH.episode.setText(item.getEpisode());
		vH.hot.setText(item.getHot());
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int p) {
		// TODO: Implement this method
		return new ViewHolder(
				LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_view, viewGroup, false));
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		TextView title, hot, episode;
		ImageView cover;
		ViewHolder(View v) {
			super(v);
			this.title = (TextView) v.findViewById(R.id.tvTitle);
			this.hot = v.findViewById(R.id.tvHot);
			this.episode = v.findViewById(R.id.tvEpisode);
			this.cover = v.findViewById(R.id.tvCover);
			cover.setOutlineProvider(new ViewOutlineProvider() {
					@Override
					public void getOutline(View view, Outline outline) {
						float radius = view.getContext()
							.getResources()
							.getDimension(R.dimen.corner_radius);
						outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
					}
				});
			cover.setClipToOutline(true);
		}
	}
}

