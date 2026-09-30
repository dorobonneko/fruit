package moe.mpi.drawable;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import moe.mpi.Image;
import android.graphics.drawable.Animatable;
import moe.mpi.Request;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable.Callback;
import moe.mpi.Mpi;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
public class MpiDrawable extends Drawable implements Animatable
{
	private Image image;
	private ScaleType type=ScaleType.FIT_CENTER;
	private Matrix matrix=new Matrix();
	private Runnable mRunnable;
	public MpiDrawable(Image image,ImageView.ScaleType type){
		this.image=image;
		this.type=type;
	}

	@Override
	public int getIntrinsicWidth()
	{
		// TODO: Implement this method
		return image.getWidth();
	}

	@Override
	public int getIntrinsicHeight()
	{
		// TODO: Implement this method
		return image.getHeight();
	}

	@Override
	public void start()
	{
		// TODO: Implement this method
		image.start();
	}

	@Override
	public void stop()
	{
		// TODO: Implement this method
		image.stop();
	}

	@Override
	public boolean isRunning()
	{
		// TODO: Implement this method
		return image.isRunning();
	}


	@Override
	public void setAlpha(int alpha)
	{
		// TODO: Implement this method
	}

@Override
public void draw(Canvas canvas)
{
 image.draw(canvas,matrix,null);
 if(image instanceof Image.ImageMovie||image instanceof Image.ImageGif){
	 if(image.isRunning()){
		 if(mRunnable==null)
			 mRunnable=new Runnable(){
				 public void run(){
					 invalidateSelf();
					 }
				 };
				 
		 postDelay(mRunnable,image.getNextFrameTime());
		 }
 }
}
public void postDelay(Runnable run,long delay){
	Callback c=getCallback();
	if(c!=null)
	c.scheduleDrawable(this,run,delay);
}
@Override
public int getOpacity()
{
	// TODO: Implement this method
	return 0;
}

@Override
public void setColorFilter(ColorFilter colorFilter)
	{
// TODO: Implement this method
}

@Override
protected void onBoundsChange(Rect bounds)
	{
super.onBoundsChange(bounds);
	switch (type) {
			// 1. 不缩放，图片居中显示（图片可能超出画布）
		case CENTER: {
				float scale = 1.0f; // 不缩放
				// 偏移量：图片中心与画布中心对齐
				float dx = (bounds.width() - image.getWidth() * scale) / 2f;
				float dy = (bounds.height() - image.getHeight() * scale) / 2f;
				matrix.reset();
				matrix.postScale(scale, scale);
				matrix.postTranslate(dx, dy);
				break;
			}

			// 2. 等比缩放，填满画布（可能裁剪图片边缘）
		case CENTER_CROP: {
				// 取较大缩放比，确保图片至少一个方向填满画布
				float scale = Math.max(bounds.width()/(float)image.getWidth(), 
									   bounds.height()/(float)image.getHeight());
				// 偏移量：图片中心与画布中心对齐（超出部分自动裁剪）
				float dx = (bounds.width() - image.getWidth() * scale) / 2f;
				float dy = (bounds.height() - image.getHeight() * scale) / 2f;
				matrix.reset();
				matrix.postScale(scale, scale);
				matrix.postTranslate(dx, dy);
				break;
			}

			// 3. 等比缩放，图片完全放入画布（图片小于画布时不缩放，直接居中）
		case CENTER_INSIDE: {
				float scale;
				// 图片本身小于画布：不缩放；否则按较小比缩放
				if (image.getWidth() <= bounds.width() && image.getHeight() <= bounds.height()) {
					scale = 1.0f;
				} else {
					scale = Math.min(bounds.width()/(float)image.getWidth(), 
									 bounds.height()/(float)image.getHeight());
				}
				// 偏移量：图片中心与画布中心对齐
				float dx = (bounds.width()- image.getWidth() * scale) / 2f;
				float dy = (bounds.height() - image.getHeight() * scale) / 2f;
				matrix.reset();
				matrix.postScale(scale, scale);
				matrix.postTranslate(dx, dy);
				break;
			}

			// 4. 非等比缩放，拉伸图片填满画布（图片比例可能变形）
		case FIT_XY: {
				// x/y方向分别按画布比例拉伸，不保持图片原比例
				float scaleX = bounds.width()/(float)image.getWidth();
				float scaleY = bounds.height()/(float)image.getHeight();
				float dx = 0f; // 拉伸后刚好填满，无需水平偏移
				float dy = 0f; // 拉伸后刚好填满，无需垂直偏移
				matrix.reset();
				matrix.postScale(scaleX, scaleY);
				matrix.postTranslate(dx, dy);
				break;
			}

			// 5. 等比缩放至完全放入画布，图片左上角与画布左上角对齐
		case FIT_START: {
				// 缩放比与FIT_CENTER一致（确保图片完全放入）
				float scale = Math.min(bounds.width()/(float)image.getWidth(), 
									   bounds.height()/(float)image.getHeight());
				float dx = 0f; // 水平左对齐，无偏移
				float dy = 0f; // 垂直上对齐，无偏移
				matrix.reset();
				matrix.postScale(scale, scale);
				matrix.postTranslate(dx, dy);
				break;
			}

			// 6. 等比缩放至完全放入画布，图片右下角与画布右下角对齐
		case FIT_END: {
				// 缩放比与FIT_CENTER一致（确保图片完全放入）
				float scale = Math.min(bounds.width()/(float)image.getWidth(), 
									   bounds.height()/(float)image.getHeight());
				// 水平右对齐：画布宽 - 图片缩放后宽度；垂直下对齐：画布高 - 图片缩放后高度
				float dx = bounds.width() - (image.getWidth() * scale);
				float dy = bounds.height() - (image.getHeight() * scale);
				matrix.reset();
				matrix.postScale(scale, scale);
				matrix.postTranslate(dx, dy);
				break;
			}

			// 原FIT_CENTER（保留作对照）
		case FIT_CENTER: {
				float scale = Math.min(bounds.width()/(float)image.getWidth(), 
									   bounds.height()/(float)image.getHeight());
				float dx = (bounds.width() - image.getWidth() * scale) / 2f;
				float dy = (bounds.height() - image.getHeight() * scale) / 2f;
				matrix.reset();
				matrix.postScale(scale, scale);
				matrix.postTranslate(dx, dy);
				break;
			}
	}
	
}





}
