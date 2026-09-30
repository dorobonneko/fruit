package moe.mpi.handle;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javax.net.ssl.*;
import moe.mpi.*;

import android.net.Uri;
import javax.security.auth.callback.Callback;
import java.util.zip.GZIPInputStream;

public class HttpRequestHandler implements RequestHandler {
	private Uri cache;
	@Override
	public boolean canHandle(Request request) {
		String scheme = request.getUrl().getScheme();
		if (scheme != null)
			switch (scheme) {
				case "http" :
				case "https" :
					return true;
			}
		return false;
	}

	@Override
	public boolean onHandle(ThreadPoolExecutor pool, final Request request, final Callback call) {
		try {
			//synchronized(Uid.getLock(request.getKey())){
			cache = onHandle(request, call);
			if (cache == null)
				throw new NullPointerException("资源获取失败");
			call.onReady(cache);
			//}
			return true;
		} catch (Exception e) {
			call.onError(e);
			return false;
		}
	}
	private Uri onHandle(Request request, Callback call) throws IOException {
		HttpURLConnection huc = null;
		OutputStream output = null;
		InputStream input = null;
		DiskCache dc = request.getMpi().getDiskCache();
		File tmp = dc.getDirty(request.getKey());
		File cache = dc.getCache(request.getKey());
		if (cache.exists()) {
			return Uri.fromFile(cache);
		}
		boolean chunked = false;
		try {
			huc = (HttpURLConnection) new URL(request.getUrl().toString()).openConnection();
			if (huc instanceof HttpsURLConnection) {
				SSLSocketFactory ssf = request.getMpi().getSSLSocketFactory();
				if (ssf != null)
					((HttpsURLConnection) huc).setSSLSocketFactory(ssf);
			}
			Iterator<Map.Entry<String, String>> header_i = request.getHeader().entrySet().iterator();
			while (header_i.hasNext()) {
				Map.Entry<String, String> entry = header_i.next();
				huc.setRequestProperty(entry.getKey(), entry.getValue());
			}
			huc.setRequestProperty("Connection", "close");
			huc.setRequestProperty("Range",
					"bytes=".concat(tmp.exists() ? String.valueOf(tmp.length()) : "0").concat("-"));
			huc.setFollowRedirects(true);
			if (request.isCancel())
				throw new CancellationException();
			huc.setConnectTimeout(15000);
			huc.setReadTimeout(10000);
			int code = huc.getResponseCode();
			if (request.isCancel())
				throw new CancellationException();
			String range = huc.getHeaderField("Content-Range");
			long length = huc.getContentLength();
			if (range != null)
				length = Long.parseLong(range.substring(range.indexOf("/") + 1));

			if (code == 416) {
				//huc.disconnect();
				tmp.delete();
				//tmp.renameTo(dc.getCache(request.getKey(), true));
				//hrs.set(dc.getCache(request.getKey()));
				return onHandle(request, call);
			}
			/*if (code == 301 || code == 302)
			{
				String location=huc.getHeaderField("Location");
				if (location != null)
				{
					request.setLocation(location);
					return onHandle(request,call);
				}
			}*/
			input = huc.getInputStream();
			if (code == 200) {
				tmp.delete();
				output = new FileOutputStream(tmp);
			} else {
				output = new FileOutputStream(tmp, true);
			}
			int len = -1;
			long current = tmp.length();
			call.onProgress(current, length);
			if ("chunked".equalsIgnoreCase(huc.getHeaderField("Transfer-Encoding"))) {
				chunked = true;
				while ((len = input.read()) != -1) {
					output.write(len);
					current++;
					call.onProgress(current, current);
					if (request.isCancel())
						throw new CancellationException();
					//output.flush();
				}
			} else {
				byte[] buff = new byte[1024 * 32];

				while ((len = input.read(buff)) != -1) {
					output.write(buff, 0, len);
					current += len;
					call.onProgress(current, length);
					if (request.isCancel())
						throw new CancellationException();
					//output.flush();
				}
			}
			output.flush();
			output.close();
			input.close();
				tmp.renameTo(cache);
			if (request.isCancel())
				throw new CancellationException();
			if (cache.exists())
				return Uri.fromFile(cache);
		} catch (CancellationException e) {
		} catch (IOException e) {
			throw e;
		} finally {
			if (huc != null)
				huc.disconnect();
			if (output != null) {
				output.flush();
				output.close();
			}
		}
		return null;
	}
}

