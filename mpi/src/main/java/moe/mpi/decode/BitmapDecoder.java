package moe.mpi.decode;

import android.graphics.*;
import moe.mpi.*;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import moe.mpi.internel.SimpleResource;

	public class BitmapDecoder implements Decoder<Bitmap>
	{
	private Context context;
	public BitmapDecoder(Context context) {
		this.context = context;
	}
	@Override
	public Resource<Bitmap> decode(BitmapPool mBitmapPool, Uri input,String key,boolean origin, int w, int h) {
		switch (input.getScheme()) {
			case "file" : {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(input.getPath(),options);
					Bitmap.Config config = Bitmap.Config.ARGB_8888;
					if (Build.VERSION.SDK_INT >= 26)
						config = options.outConfig;
					options.inPreferredConfig = config == null ? Bitmap.Config.ARGB_8888 : config;
					options.inJustDecodeBounds = false;
					options.inSampleSize =calculateInSampleSize(options.outWidth,options.outHeight, w, h);
					options.inBitmap = options.inSampleSize==1?mBitmapPool.getBitmap(options.outWidth, options.outHeight,
																					 options.inPreferredConfig):null;
					options.inMutable = true;
					Bitmap bitmap = BitmapFactory.decodeFile(input.getPath(), options);
					if (bitmap != options.inBitmap) {
						mBitmapPool.recycle(options.inBitmap);
					}
					if(bitmap!=null)
					return new SimpleResource<Bitmap>(key,bitmap);
					break;
				}
			case "drawable" : {
					int id = Integer.parseInt(input.getSchemeSpecificPart());
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inDensity = Bitmap.DENSITY_NONE;
					options.inScaled = false;
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeResource(context.getResources(), id, options);
					if (options.outWidth <= 0 || options.outHeight <= 0) {
						Drawable d = context.getResources().getDrawable(id, context.getTheme());
						if (d == null)
							return null;
						if (w == 0 || h == 0) {
							w = d.getIntrinsicWidth();
							h = d.getIntrinsicHeight();
						}
						Bitmap b = mBitmapPool.getBitmap(w, h, Bitmap.Config.ARGB_8888);
						Canvas canvas = new Canvas(b);
						d.setBounds(0, 0, w, h);
						d.draw(canvas);
						return new SimpleResource<Bitmap>(key,b);
					}
					options.inJustDecodeBounds = false;
					Bitmap.Config config = Bitmap.Config.ARGB_8888;
					if (Build.VERSION.SDK_INT >= 26)
						config = options.outConfig;
					if (config == null)
						config = Bitmap.Config.ARGB_8888;
					options.inBitmap = mBitmapPool.getBitmap(options.outWidth, options.outHeight, config);
					options.inMutable = true;
					options.inPreferredConfig = config;
					options.inSampleSize =origin?1:calculateInSampleSize(options.outWidth,options.outHeight, w, h);
					return new SimpleResource<Bitmap>(key,BitmapFactory.decodeResource(context.getResources(), id, options));

				}
		}
		return null;
	}
	public static int calculateInSampleSize(int imgW,int imgH, int reqWidth, int reqHeight) {
		if(reqWidth==0&&reqHeight==0)
			return 1;
		int inSampleSize=1;
		if(reqWidth==0){
			while(imgH>reqHeight){
				inSampleSize*=2;
				imgH/=2;
			}
			return inSampleSize;
		}else if(reqHeight==0){
			while(imgW>reqWidth){
				inSampleSize*=2;
				imgW/=2;
			}
			return inSampleSize;
		}
		while(imgW>reqWidth&&imgH>reqHeight){
			inSampleSize*=2;
			imgW/=2;
			imgH/=2;
		}
		return inSampleSize;
	}

}
