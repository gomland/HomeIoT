package my.home.mobileiot.web.tools;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

import android.util.Log;

/**
 * Created by Sung Shin on 2016-08-26.
 */
public class LocalIPAddress {
    private static final String TAG = "LocalIP";
    
    public static String getLocal() {
    	Socket socket = null;
     	String connectedIpAddr = null;
     	     	
 		try {
 			socket = new Socket("www.google.com", 80);
 			connectedIpAddr = socket.getLocalAddress().getHostAddress();
 		} catch (Exception e) {
 			Log.d(TAG, "Exception :" + e.getMessage());
 		} finally{
 			if(socket != null)
 				try {
 					socket.close();
 				} catch (IOException e) {
 					e.printStackTrace();
 				}
 		}
     	Log.d(TAG, "Dectecting ip :" + connectedIpAddr);
     	
     	return connectedIpAddr;
    }

    public static ArrayList<String> getEhternetList() {
    	ArrayList<String> ipList = new ArrayList<String>();
                
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    
                    if(!(inetAddress instanceof Inet6Address)){ //ip v6는 회피 -> 가상 ip인 192.0.0.4를 사용한다. (v4-rmnet1 type)
	                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress() && !inetAddress.isLinkLocalAddress()) { //wlan0 case      
	                    	ipList.clear();  //wifi일 경우 다른 ip는 필요가 없기 떄문에 제거
	                    	ipList.add(inetAddress.getHostAddress());
	                    	Log.d(TAG, "find : " +  inetAddress.getHostAddress() + "[" + intf.getDisplayName() + "]");
	                    	return ipList;
	                    }
	                    else if(!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && !inetAddress.isSiteLocalAddress()){ //lte, 3g, 4g case, v4-rmnet1
	                    	ipList.add(inetAddress.getHostAddress());
	                    	Log.d(TAG, "find : " +  inetAddress.getHostAddress() + "[" + intf.getDisplayName() + "]");
	                    }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.d(TAG, "get Local IP Address exception : " + ex.toString());
        }
                
        return ipList;
    }
    
    public static String getSearchByEhternetName() {
        String localIP = null;
        
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    Log.d(TAG, "item : " + intf.getDisplayName() + "," + inetAddress.getHostAddress());
                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress() && !inetAddress.isLinkLocalAddress() ||
                            !inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && !inetAddress.isSiteLocalAddress()) {
                    	
                    	String displayName = intf.getDisplayName();                    	
                    	if(displayName.equals("wlan0") || displayName.equals("v4-rmnet1")){
                    		localIP = inetAddress.getHostAddress();
                    		Log.d(TAG, "<find ip> : " +  localIP);
                    		return localIP;
                    	}
                    	else if(displayName.equals("rmnet1"))
                    		localIP = inetAddress.getHostAddress();                    	
                    }
                }
            }
        } catch (SocketException ex) {
            Log.d(TAG, "get Local IP Address exception : " + ex.toString());
        }
        
        Log.d(TAG, "<find ip> : " +  localIP);
        
        return localIP;
    }
    
    private static String getV6ToV4(String inet6HosAddress){
    	//inet6HosAddress = "2001:2d8:33f:5016::5372:b0a5%2";
    	StringTokenizer st = new StringTokenizer(inet6HosAddress, ":");
    	String ip4ToHex = "";
    	String ip4ToStr = null;
    	int cnt = 0;
    	try{
	    	while(st.hasMoreTokens()){
	    		String str =  st.nextToken();
	    		if(cnt == 4)
	    			ip4ToHex += str;
	    		if(cnt == 5)
	    			ip4ToHex += str.substring(0, 4);
	    		cnt++;
	    	}
    	    	
	    	Log.d(TAG, "Hex ip4 : " + ip4ToHex + ", length : " + ip4ToHex.length());    	
	    	
	    	if(ip4ToHex.length() == 8){
	    		int i=0;
	    		ip4ToStr="";
	    		
	    		for(; i<6; i+=2)
	    			ip4ToStr += (Integer.parseInt(ip4ToHex.substring(i, i+2), 16) + ".");
	    		ip4ToStr += (Integer.parseInt(ip4ToHex.substring(i, i+2), 16));
	    	}	    	
	    	Log.d(TAG, "Hex ip4 : " + ip4ToStr);  
    	}catch(Exception e){
    		Log.d(TAG, e.getMessage());
    	}
    	
    	return ip4ToStr;
    }
}
