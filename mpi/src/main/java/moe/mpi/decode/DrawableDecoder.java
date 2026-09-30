package moe.mpi.decode;
import moe.mpi.Decoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import moe.mpi.BitmapPool;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.graphics.BitmapFactory;
import moe.mpi.Resource;
import moe.mpi.bitmap.BitmapResource;
import moe.mpi.drawable.DrawableResource;
import moe.mpi.internel.SimpleResource;

	public class DrawableDecoder implements Decoder<Drawable>
	{
		private Context context;
		public DrawableDecoder(Context context){
			this.context=context;
		}
	@Override
		public Resource<Drawable> decode(BitmapPool mBitmapPool, Uri input, String key, boolean origin, int w, int h)
		{
			switch(input.getScheme()){
				case "file":{
					Drawable d=Drawable.createFromPath(input.getPath());
					if(d instanceof BitmapDrawable&&!origin){
						Bitmap b=decodeBitmap(input,mBitmapPool,w,h);
						if(b==null)
							return null;
						else{
							((BitmapDrawable)d).getBitmap().recycle();
							return new BitmapResource(context.getResources(),mBitmapPool,new SimpleResource<Bitmap>(key,b));
						}
					}else if(d!=null){
						return new DrawableResource<Drawable>(key,mBitmapPool,d);
					}
					break;
				}case "drawable":
					int id = Integer.parseInt(input.getSchemeSpecificPart());
					Drawable d;
					if(Build.VERSION.SDK_INT>=21)
					d=context.getTheme().getDrawable(id);
					else
						d=context.getResources().getDrawable(id);
					/*Bitmap b=mBitmapPool.getBitmap(d.getIntrinsicWidth(),d.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
					Canvas c=new Canvas(b);
					d.setBounds(0,0,b.getWidth(),b.getHeight());
					d.draw(c);*/
					if(d!=null)
					return new DrawableResource<>(key,mBitmapPool,d);
					
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
	
	private Bitmap decodeBitmap(Uri input,BitmapPool mBitmapPool,int w,int h){
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
		return bitmap;
	}
}
