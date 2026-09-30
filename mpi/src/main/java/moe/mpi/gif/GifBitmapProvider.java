package moe.mpi.gif;
import com.bumptech.glide.gifdecoder.GifDecoder;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import moe.mpi.BitmapPool;

public class GifBitmapProvider implements GifDecoder.BitmapProvider {
	private BitmapPool bp;
	public GifBitmapProvider(BitmapPool bp) {
		this.bp = bp;
	}
	@Override
	public byte[] obtainByteArray(int p) {
		// TODO: Implement this method
		return new byte[p];
	}

	@Override
	public int[] obtainIntArray(int p) {
		// TODO: Implement this method
		return new int[p];
	}

	@Override
	public Bitmap obtain(int p, int p1, Bitmap.Config config) {
		// TODO: Implement this method
		return bp.getBitmap(p, p1, config);
	}

	@Override
	public void release(Bitmap bitmap) {
		// TODO: Implement this method
		bp.recycle(bitmap);
	}

	@Override
	public void release(byte[] p) {
		// TODO: Implement this method
	}

	@Override
	public void release(int[] p) {
		// TODO: Implement this method
	}

}

