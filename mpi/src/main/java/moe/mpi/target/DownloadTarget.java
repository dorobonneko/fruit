package moe.mpi.target;

import java.io.*;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import java.nio.file.Files;
import moe.mpi.Target;
import moe.mpi.target.DownloadTarget;
import moe.mpi.Request;
import moe.mpi.SizeReady;
import moe.mpi.Resource;
import moe.mpi.util.Size;
import java.nio.channels.FileChannel;

public class DownloadTarget extends SimpleTarget<File>
{
	private File output;
	public DownloadTarget(File output)
	{
		this.output = output;
		File parent=output.getParentFile();
		if (!parent.exists())
			parent.mkdirs();
		else if (parent.isFile())
		{
			parent.delete();
			parent.mkdirs();
		}
	}

@Override
public void onSuccess(Resource<? extends File> pd)
	{
		
		File f=pd.acquire();
		if(f!=null){
		try
		{
			copyFile(f,output);
			getRequest().cancel();
			if(l!=null)l.onSuccess(output);
		}
		catch (IOException e)
		{
			error(e,null);
		}
	}
}

	public static void copyFile(File srcPath, File destPath) throws IOException {
		try (FileInputStream fis = new FileInputStream(srcPath);
		FileOutputStream fos = new FileOutputStream(destPath);
		FileChannel srcChannel = fis.getChannel();
		FileChannel destChannel = fos.getChannel()) {
			// 直接传输通道数据，底层优化，效率高
			destChannel.transferFrom(srcChannel, 0, srcChannel.size());
		}
		
	}

	@Override
	public void removeCallback(SizeReady sr)
	{
		// TODO: Implement this method
	}

	
}
