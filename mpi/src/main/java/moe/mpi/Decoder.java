package moe.mpi;
import java.io.InputStream;
import java.io.File;
import android.graphics.Bitmap;
import android.net.Uri;

public interface Decoder<Z>
{
	public Resource<Z> decode(BitmapPool mBitmapPool,Uri input,String key,boolean origin,int w,int h);
}
