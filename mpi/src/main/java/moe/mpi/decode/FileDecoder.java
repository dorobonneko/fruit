package moe.mpi.decode;
import moe.mpi.Decoder;
import android.net.Uri;
import moe.mpi.Image;
import moe.mpi.BitmapPool;
import java.io.File;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import moe.mpi.Mpi;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import moe.mpi.Resource;
import moe.mpi.internel.SimpleResource;

public class FileDecoder implements Decoder<File> {
	private Context theme;
	private Mpi mpi;
	public FileDecoder(Context c, Mpi mpi) {
		theme = c;
		this.mpi = mpi;
	}
	@Override
	public Resource<File> decode(BitmapPool mBitmapPool, Uri input, String key, boolean origin, int w, int h) {
		switch (input.getScheme()) {
			case "drawable" :
				//保存到本地文件
				int id = Integer.parseInt(input.getSchemeSpecificPart());
				Drawable d = theme.getResources().getDrawable(id);
				Bitmap b = mBitmapPool.getBitmap(w, h, Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(b);
				d.setBounds(0, 0, w, h);
				d.draw(canvas);
				File cache = mpi.getDiskCache().getCache(key);
				try {
					FileOutputStream fos = new FileOutputStream(cache);
					b.compress(Bitmap.CompressFormat.PNG, 100, fos);
					try {
						fos.close();
					} catch (IOException e) {
					}
					mBitmapPool.recycle(b);
					return new SimpleResource<File>(key,cache);
				} catch (FileNotFoundException e) {
				}
				break;
			case "file" :
				return new SimpleResource<File>(key,new File(input.getPath()));
		}
		return null;
	}

}

