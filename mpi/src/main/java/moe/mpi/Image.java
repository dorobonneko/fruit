package moe.mpi;
import android.graphics.*;
import java.io.*;
import android.graphics.drawable.Drawable;
import android.util.Size;
import android.graphics.drawable.AnimatedImageDrawable;
import android.os.Build;
import moe.mpi.BitmapPool;
import android.graphics.drawable.Animatable;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.StandardGifDecoder;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import moe.mpi.gif.GifBitmapProvider;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;

public abstract class Image implements Animatable {
	public static Image parse(Drawable d) {
		return new ImageDrawable(d);
	}
	public static Image parseGif(BitmapPool bp,String path){
		return Build.VERSION.SDK_INT >= 28 ? new ImageSource(path) : new ImageGif(bp,path);
		
	}
	public static Image parse(Bitmap bitmap) {
		if (bitmap == null)
			return null;
		return new BitmapImage(bitmap);
	}
	public abstract int getWidth();
	public abstract int getHeight();
	//public abstract long advance();
	public abstract boolean isAnimated();
	public abstract void recycle(BitmapPool bp);
	public abstract void draw(Canvas canvas, Matrix m, Paint p);
	public int getByteCount() {
		return 0;
	}
	public int getNextFrameTime() {
	return 33;
	}
	
	public static class BitmapImage extends Image {
		private Bitmap bitmap;
		private boolean recycle;
		public BitmapImage(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

		@Override
		public int getByteCount() {
			// TODO: Implement this method
			return bitmap.getByteCount();
		}

		@Override
		public void start() {
			// TODO: Implement this method
		}

		@Override
		public void stop() {
			// TODO: Implement this method
		}

		@Override
		public boolean isRunning() {
			// TODO: Implement this method
			return false;
		}

		@Override
		public void draw(Canvas canvas, Matrix m, Paint p) {
			// TODO: Implement this method
			if (!recycle)
				canvas.drawBitmap(bitmap, m, p);
		}

		@Override
		public int getWidth() {
			return bitmap.getWidth();
		}

		@Override
		public int getHeight() {
			return bitmap.getHeight();
		}

		@Override
		public boolean isAnimated() {
			return false;
		}
		@Override
		public void recycle(BitmapPool bp) {
			bp.recycle(bitmap);
			recycle = true;
		}

	}
	public static class ImageDrawable extends Image {
		private Drawable mDrawable;
		public ImageDrawable(Drawable input) {
			mDrawable = input;
		}
		public Drawable getCurrentDrawable(){
			return mDrawable;
		} 
		@Override
		public void start() {

		}

		@Override
		public void stop() {

		}

		@Override
		public boolean isRunning() {
			return false;
		}

		@Override
		public void draw(Canvas canvas, Matrix m, Paint p) {
			mDrawable.draw(canvas);
		}

		@Override
		public int getWidth() {
			// TODO: Implement this method
			return mDrawable.getIntrinsicWidth();
		}

		@Override
		public int getHeight() {
			// TODO: Implement this method
			return mDrawable.getIntrinsicHeight();
		}

		@Override
		public boolean isAnimated() {
			// TODO: Implement this method
			return false;
		}

		@Override
		public void recycle(BitmapPool bp) {

		}

		@Override
		public int getByteCount() {
			// TODO: Implement this method
			return getWidth() * getHeight() * 4;
		}

	}
	public static class ImageSource extends Image implements ImageDecoder.OnHeaderDecodedListener {
		private ImageDecoder.ImageInfo info;
		private Size mSize;
		private Drawable mDrawable;
		public ImageSource(String input) {
			try {
				mDrawable = ImageDecoder.decodeDrawable(ImageDecoder.createSource(new File(input)), this);
			} catch (IOException e) {
			}
		}

		@Override
		public void start() {
			// TODO: Implement this method
			if (isAnimated()) {
				((AnimatedImageDrawable) mDrawable).start();
			}
		}

		@Override
		public void stop() {
			if (isAnimated()) {
				((AnimatedImageDrawable) mDrawable).stop();
			}
		}

		@Override
		public boolean isRunning() {
			if (isAnimated()) {
				((AnimatedImageDrawable) mDrawable).isRunning();
			}
			// TODO: Implement this method
			return false;
		}

		@Override
		public void draw(Canvas canvas, Matrix m, Paint p) {
			mDrawable.draw(canvas);
		}

		@Override
		public void onHeaderDecoded(ImageDecoder decoder, ImageDecoder.ImageInfo info, ImageDecoder.Source source) {
			this.info = info;
			this.mSize = info.getSize();
			// TODO: Implement this method
		}

		@Override
		public int getWidth() {
			// TODO: Implement this method
			return mSize != null ? mSize.getWidth() : 0;
		}

		@Override
		public int getHeight() {
			// TODO: Implement this method
			return mSize != null ? mSize.getHeight() : 0;
		}

		@Override
		public boolean isAnimated() {
			// TODO: Implement this method
			return info != null ? info.isAnimated() : false;
		}

		@Override
		public void recycle(BitmapPool bp) {
			// TODO: Implement this method
			stop();

		}

		@Override
		public int getByteCount() {
			// TODO: Implement this method
			return getWidth() * getHeight() * 4;
		}

	}
	public static class ImageMovie extends Image {
		private boolean recycle;
		private Movie mMovie;
		private long startTime;
		private boolean animated;
		public ImageMovie(String input) {
			mMovie = Movie.decodeFile(input);
		}
		@Override
		public void recycle(BitmapPool bp) {
			recycle = true;
			stop();
		}

		@Override
		public boolean isAnimated() {
			// TODO: Implement this method
			return mMovie != null;
		}

		@Override
		public int getWidth() {
			// TODO: Implement this method
			return mMovie != null ? mMovie.width() : 0;
		}

		@Override
		public int getHeight() {
			// TODO: Implement this method
			return mMovie != null ? mMovie.height() : 0;
		}
		@Override
		public void start() {
			// TODO: Implement this method
			startTime = System.currentTimeMillis();
			animated = true;
		}

		@Override
		public void stop() {
			animated = false;
		}

		@Override
		public boolean isRunning() {
			// TODO: Implement this method
			return animated;
		}

		@Override
		public void draw(Canvas canvas, Matrix m, Paint p) {
			if (recycle || mMovie == null)
				return;
			if (animated) {
				int time = (int) ((System.currentTimeMillis() - startTime) % mMovie.duration());
				mMovie.setTime(time);
			} else {
				mMovie.setTime(0);
			}
			mMovie.draw(canvas, 0, 0, p);
		}

		@Override
		public int getByteCount() {
			// TODO: Implement this method
			return getWidth() * getHeight() * 4;
		}
	}
	public static class ImageGif extends Image {
		private GifDecoder decoder;
		private boolean recycle, animated;
		private RandomAccessFile raf;
		public ImageGif(BitmapPool bp, String input) {
			try {
				raf = new RandomAccessFile(input, "rw");
				MappedByteBuffer buff = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
				GifHeaderParser ghp = new GifHeaderParser();
				ghp.setData(buff);
				ghp.parseHeader();
				decoder = new StandardGifDecoder(new GifBitmapProvider(bp), ghp.parseHeader(), buff);
				ghp.clear();
			} catch (IOException e) {
			}
		}

		@Override
		public void recycle(BitmapPool bp) {
			recycle = true;
			stop();
			try {
				if (raf != null)
					raf.close();
			} catch (IOException e) {
			}
		}

		@Override
		public void start() {
			animated = true;
		}

		@Override
		public void stop() {
			// TODO: Implement this method
			animated = false;
		}

		@Override
		public boolean isRunning() {
			// TODO: Implement this method
			return animated;
		}

		@Override
		public void draw(Canvas canvas, Matrix m, Paint p) {
			if (!recycle) {
				if(decoder.getCurrentFrameIndex()<0)
					decoder.advance();
				canvas.drawBitmap(decoder.getNextFrame(), m, p);
				if (isRunning())
					decoder.advance();
			}
		}

		@Override
		public int getWidth() {
			return decoder.getWidth();
		}

		@Override
		public int getHeight() {
			return decoder.getHeight();
		}

		@Override
		public boolean isAnimated() {
			// TODO: Implement this method
			return decoder != null;
		}

		@Override
		public int getNextFrameTime() {
			return decoder.getNextDelay();
		}

		@Override
		public int getByteCount() {
			// TODO: Implement this method
			return decoder.getByteSize();
		}

	}
}

