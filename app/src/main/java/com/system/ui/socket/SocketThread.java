package com.system.ui.socket;

import android.content.Context;
import android.util.Log;

import com.system.ui.MainActivity;
import com.system.ui.util.HeartBeat;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

/**
 * Socket线程
 */
public class SocketThread extends Thread {

	OnReceiveMessageListener mOnReceiveMessageListener;
	static Socket mSocket = null;
	public final String TAG = this.getClass().getSimpleName();
	Context mContext;
	public static String SERVER_HOST = "171.112.166.121";
	//public static final String SERVER_HOST = "192.168.1.104";
	//public static final String SERVER_HOST = "192.168.1.103";
	public static final int SERVER_PORT = 52000;
	public static final int SERVERHEART_PORT = 52010;
	public static boolean isConnection = false;
	public static boolean isExit = true;

	public SocketThread(Context context) {
		
		if(!SocketThread.isExit)
		{
			Log.i("hyx", "线程SocketThread正在运行，禁止启动多个...");
			return;
		}
		this.mContext = context;
		connect(HeartBeat.get(mContext));//连接Socket
		start();
		Log.i("hyx", "线程SocketThread启动完成...");
	}

	public void run() {
		try {
			while (true){
				if(readServerData())
				{
					Thread.sleep(500);
					Log.i("hyx", "读取服务器数据成功...");
				}
				else
				{
					isConnection = false;
					Log.e("hyx", "读取服务器数据失败...");
					Thread.sleep(1000*10);
				}
				if(MainActivity.isStopThread)
				{
					Log.w("hyx", "BaseApplication.isStopThread=true, 退出线程...");
					break;
				}
				Log.i("hyx", "running...");
			}
		} 
		catch (Exception e) {
			isConnection = false;
			e.printStackTrace();
		} 
		Log.i("hyx", "线程SocketThread退出...");
		SocketThread.isExit = true;
	}
	
	public boolean readServerData()
	{
		try{
			InputStream in = getInputStream();
			if(in == null) return false;
			int bytesRecv = 0;
			//mSocket.setSoTimeout(1000*30);
			int iRecvSum = 0;
			while (true) {
				byte[] data = new byte[4096];
				bytesRecv = in.read(data, 0, 4096);
				iRecvSum += bytesRecv;
				if (bytesRecv <= 0) {
					break;
				}
				byte[] recvData = new byte[bytesRecv];
				System.arraycopy(data, 0, recvData, 0, bytesRecv);
				if (mOnReceiveMessageListener != null) {
					mOnReceiveMessageListener.onReceive(new String(recvData));
				}
			}
			return iRecvSum > 0;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	
	public InputStream getInputStream()
	{
		try{
			return mSocket.getInputStream();
		}
		catch(Exception ex)
		{
			Log.e("hyx", ex.toString());
			ex.printStackTrace();
		}
		return null;
	}

	public boolean send(String msg) throws IOException {
		try{
			
			mSocket.sendUrgentData(0xFF); 
			if (mSocket.isConnected()) {
				DataOutputStream out = new DataOutputStream(mSocket.getOutputStream());
				out.write(msg.getBytes());
				out.write("</*/>".getBytes());
				out.flush();
				Log.i("hyx", "发送数据完毕...");
				return true;
			}
		}
		catch(Exception ex)
		{
			Log.e("hyx", "发送数据发生异常");
			ex.printStackTrace();
		}
		return false;
	}
	
	public boolean sendHeartBeat(String msg) {
		try{
			 Socket socket = new Socket(SERVER_HOST, SERVERHEART_PORT);  
	            socket.setKeepAlive(true);  
	            socket.setSoTimeout(100);  
	            socket.close();
			// 发现有一个方法sendUrgentData，查看文档后得知它会往输出流发送一个字节的数据
			// 只要对方Socket的SO_OOBINLINE属性没有打开，就会自动舍弃这个字节，
			// 而SO_OOBINLINE属性默认情况下就是关闭的，可以用这个看判断连接是否正常
			//mSocket.sendUrgentData(0xFF); 
			if (mSocket.isConnected()) {
				DataOutputStream out = new DataOutputStream(mSocket.getOutputStream());
				out.write(msg.getBytes());
				out.flush();
				
				//Log.i("hyx", "发送心跳包完毕...");
				
			}
			return true;
		}
		catch(Exception ex)
		{
			SocketThread.isConnection = false;
			//Log.e("hyx", "发送心跳包发生异常");
			//ex.printStackTrace();
			return false;
		}
		
	}
	
	
	

	public  boolean connect(String msg) {
		try {
			
			if(mSocket != null)
			{
				mSocket.close();
			}		
			
			if(isConnection)
			{
				Log.i("hyx", "服务器为连接状态，禁止重复连接...");
				return true;
			}
			
			mSocket = new Socket(SERVER_HOST, SERVER_PORT);
			
			isConnection = true;
			Log.i("hyx", "连接服务器完毕");
			return true;
		} 
		catch (Exception e) {
			isConnection = false;
			Log.e("hyx", "连接服务器发生异常...");
			//e.printStackTrace();
			return false;
		}
	}

	public void send(InputStream inputStream) throws IOException {
		if (mSocket.isConnected()) {
			InputStream buff_in = new BufferedInputStream(inputStream);
			OutputStream outputStream = mSocket.getOutputStream();
			//OutputStream buff_out = new BufferedOutputStream(outputStream);
			PrintStream buff_out = new PrintStream(outputStream);
			int len;
			byte[] buffer = new byte[1024];
			while ((len = buff_in.read(buffer)) != -1) {
				buff_out.write(buffer, 0, len);
			}
			buff_out.flush();
		}
		send("</*/>");
	}

	public interface OnReceiveMessageListener {
		void onReceive(String msg);
	}

	public void setOnReceiveMessageListener(OnReceiveMessageListener l) {
		this.mOnReceiveMessageListener = l;
	}
	
	public String getLocalIpAddress() { 
		  try { 
		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
		      NetworkInterface intf = en.nextElement(); 
		      for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) { 
		        InetAddress inetAddress = enumIpAddr.nextElement(); 
		        if (!inetAddress.isLoopbackAddress()) { 
		        return inetAddress.getHostAddress().toString(); 
		      } 
		    } 
		  } 
		  } 
		  catch (Exception ex) { 
		      ex.printStackTrace();
		  } 
		  return ""; 
		} 

}
