package moe.mpi.target;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import moe.mpi.drawable.MpiDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import moe.mpi.drawable.TextDrawable;
import java.util.Arrays;
import android.graphics.drawable.Animatable;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Bitmap;
import moe.mpi.Resource;
import moe.mpi.SizeReady;

public class ImageViewTarget extends ViewTarget<ImageView> {
	Animatable mAnimatable;
	public ImageViewTarget(ImageView view) {
		super(view);
	}
	@Override
	public void onSuccess(Resource pd) {
		if (getView() != null) {
			Object obj = pd.acquire();
			if (obj instanceof Animatable)
				mAnimatable = (Animatable) obj;

			if (obj instanceof Drawable)
				getView().setImageDrawable((Drawable) obj);
			else if (obj instanceof Bitmap)
				getView().setImageBitmap((Bitmap) obj);
			else
				getView().setImageDrawable(null);
		}
		if (mAnimatable != null)
			mAnimatable.start();
	}

	@Override
	public void error(Throwable e, Drawable d) {
		if (getView() != null)
			((ImageView) getView()).setImageDrawable(d);
	}

	@Override
	public void placeHolder(Drawable placeHolder) {
		if (getView() != null)
			((ImageView) getView()).setImageDrawable(placeHolder);
	}

	@Override
	public void removeCallback(SizeReady sr) {
		// TODO: Implement this method
		super.removeCallback(sr);
		if (mAnimatable != null)
			mAnimatable.stop();
		mAnimatable = null;
	}

}

