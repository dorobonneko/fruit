package moe.mpi.util;

import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;

public class SslSocketUtils {
    private static SSLSocketFactory mSSLSocketFactory;
    public static SSLSocketFactory getSSLSocketFactory(){
        if(mSSLSocketFactory==null)
            synchronized(SSLSocketFactory.class){
                if(mSSLSocketFactory==null)
                    try{
                        SSLContext sslc=SSLContext.getInstance("TLS");
                        sslc.init(null, new TrustManager[]{new X509TrustManager(){

                                          @Override
                                          public void checkClientTrusted(X509Certificate[] p1, String p2) throws CertificateException
                                          {
                                              // TODO: Implement this method
                                          }

                                          @Override
                                          public void checkServerTrusted(X509Certificate[] p1, String p2) throws CertificateException
                                          {
                                              // TODO: Implement this method
                                          }

                                          @Override
                                          public X509Certificate[] getAcceptedIssuers()
                                          {
                                              // TODO: Implement this method
                                              return new X509Certificate[0];
                                          }
                                      }}, new SecureRandom());
                        mSSLSocketFactory=(sslc.getSocketFactory());
                    }catch(Exception e){}
            }
        return mSSLSocketFactory;
	}
    
}
