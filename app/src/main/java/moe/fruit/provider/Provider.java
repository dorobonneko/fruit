package moe.fruit.provider;
import java.util.*;
import moe.fruit.data.*;
import okhttp3.*;
import moe.fruit.util.*;
import javax.net.ssl.*;
import java.security.cert.*;

	public interface Provider<T extends Item>{
			static OkHttpClient ohc=new OkHttpClient.Builder().sslSocketFactory(SSL.getFactory(),new X509TrustManager(){

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
				{
			// TODO: Implement this method
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
				{
			// TODO: Implement this method
			}

			@Override
			public X509Certificate[] getAcceptedIssuers()
				{
				// TODO: Implement this method
			return new X509Certificate[0];
		}
}).build();

	 List<? extends Item> getData(T arg);
	void reset();
	int getPage();
	default OkHttpClient getOkhttp(){
		 return ohc;
	}
}
