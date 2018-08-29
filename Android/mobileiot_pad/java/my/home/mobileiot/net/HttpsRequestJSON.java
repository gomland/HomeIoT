package my.home.mobileiot.net;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsRequestJSON {
	private Handler mHandler;
	private HttpListener mCallBack;

	public HttpsRequestJSON(){
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				synchronized(mCallBack){
					if(mCallBack != null){
						mCallBack.httpRequestListener(msg.what, (String)msg.obj);
						mCallBack = null;
					}
				}
			}
		};
	}
	
	public void sendHttpPostJSON(final String methodType, final String address, final JSONObject header, final JSONObject body, final HttpListener callback){
		new Thread(){
			public void run(){
				OutputStream os   = null;
				InputStream is   = null;
				ByteArrayOutputStream baos = null;
				mCallBack = callback;
				
				try{					
					URL url = new URL(address);
					trustAllHosts();
					
					HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
					httpsURLConnection.setHostnameVerifier(new HostnameVerifier(){
						@Override
						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					});
					
					HttpURLConnection connection = httpsURLConnection;
					connection.setRequestMethod(methodType);
					connection.setDoInput(true);
					
					if(header != null){
						Iterator headers = header.keys();
						
						while(headers.hasNext()){
							String key = (String) headers.next();
							connection.setRequestProperty(key, header.getString(key));
						}						
					}
					
					if(body != null){
						connection.setDoOutput(true);
						os = connection.getOutputStream();
						os.write(body.toString().getBytes());
						os.flush();
					}
					else
						connection.setDoOutput(false);
					
					String response;
					 
					int responseCode = connection.getResponseCode();
					Message msg = new Message();		
					msg.what = responseCode;
					
					if(responseCode == HttpURLConnection.HTTP_OK) {					 
					    is = connection.getInputStream();
					    baos = new ByteArrayOutputStream();
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    Log.d("http",  response);
					   	
					    msg.obj =  response;
					}
					else
						Log.d("http",  "통신 실패..." + responseCode);
					
					mHandler.sendMessage(msg);					
				}catch(Exception e){
					Log.d("http",  e.getMessage());
				}finally{
					try{
						if(os != null)
							os.close();
						if(baos != null)
							baos.close();
						if(is != null)
							is.close();
					}catch(Exception e){
						Log.d("http",  "close : " + e.getMessage());
					}						
				}
			}
		}.start();
	}	
	
	private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
