package moe.mpi.decode;
import android.graphics.*;
import moe.mpi.*;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import moe.mpi.internel.SimpleResource;

public class ImageDecoder implements Decoder<Image> {
	private Context context;
	public ImageDecoder(Context context) {
		this.context = context;
	}
	@Override
	public Resource<Image> decode(BitmapPool mBitmapPool, Uri input,String key, boolean origin, int w, int h) {
		switch (input.getScheme()) {
			case "file" : {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inDither = true;
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(input.getPath(), options);
				try {
					if ("image/gif".equalsIgnoreCase(options.outMimeType))
						return new SimpleResource<Image>(key,Image.parseGif(mBitmapPool, input.getPath()));
				} catch (Exception e) {
					return null;
				}
				Bitmap.Config config = Bitmap.Config.ARGB_8888;
				if (Build.VERSION.SDK_INT >= 26)
					config = options.outConfig;
				options.inPreferredConfig = config == null ? Bitmap.Config.ARGB_8888 : config;
				options.inJustDecodeBounds = false;
				options.inSampleSize = origin ? 1 : calculateInSampleSize(options.outWidth,options.outHeight, w, h);
				options.inBitmap = mBitmapPool.getBitmap(options.outWidth, options.outHeight,
						options.inPreferredConfig);
				options.inMutable = true;
				Bitmap bitmap = BitmapFactory.decodeFile(input.getPath(), options);
				if (bitmap == null) {
					mBitmapPool.recycle(options.inBitmap);
					return null;
				}
				if (options.outMimeType == null || options.outWidth != bitmap.getWidth()
						|| options.outHeight != bitmap.getHeight()) {
					mBitmapPool.recycle(bitmap);
					bitmap = null;
				} else if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
					mBitmapPool.recycle(bitmap);
					bitmap = null;
				}
				if (bitmap == null)
					return null;
				return new SimpleResource<Image>(key,Image.parse(bitmap));
			}
			case "drawable" : {
				int id = Integer.parseInt(input.getSchemeSpecificPart());
				return new SimpleResource<Image>(key,Image.parse(context.getResources().getDrawable(id)));
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

