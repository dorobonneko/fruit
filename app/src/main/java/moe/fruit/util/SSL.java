package moe.fruit.util;
import java.security.*;
import javax.net.ssl.*;
import java.security.cert.*;

public class SSL{
	    public static SSLSocketFactory getFactory(){
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
                        return sslc.getSocketFactory();
                    }catch(Exception e){}
        return null;
	}
	}
