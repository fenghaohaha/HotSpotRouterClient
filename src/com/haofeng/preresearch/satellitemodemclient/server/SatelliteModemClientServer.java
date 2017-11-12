package com.haofeng.preresearch.satellitemodemclient.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.haofeng.preresearch.satellitemodemclient.MainActivity;
import com.haofeng.preresearch.satellitemodemclient.config.ModemConfig;
import com.haofeng.preresearch.shotspotsetting.server.command.BaseCommand;
import com.haofeng.preresearch.shotspotsetting.server.command.CMDS;
import com.haofeng.preresearch.shotspotsetting.server.command.CommandHandler;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class SatelliteModemClientServer {
	
	private Context mContext;
	private Handler mHandler;
	private Socket mSocket;
	
	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	private BufferedReader reader;
	private PrintWriter writer;
	
	private boolean isAuthened = false;
	private boolean isConnected = false;
	private boolean isCalling = false;
	
	public static final String KEY_BUNDLE_MSG = "key_bundle_msg";
	public static final String KEY_BUNDLE_ISEXCEPTION = "key_bundle_is_exception";
	public static final String KEY_BUNDLE_ISSUCCESS = "key_bundle_is_success";
	public static final String KEY_BUNDLE_ISREQUEST = "key_bundle_is_request";
	
	private static SatelliteModemClientServer instance = null;
	
	private SatelliteModemClientServer(Context context, Handler handler) {
		this.mContext = context;
		this.mHandler = handler;
	}
	
	public synchronized static SatelliteModemClientServer getInstance(Context context, Handler handler){
		if(instance == null){
			instance = new SatelliteModemClientServer(context, handler);
		}
		
		return instance;
	}
	
	
	public void initConnect(){
		
		new ClientThread().start();
		
	}
	
	/**
	 * 客户端线程
	 * @author fenghao
	 *
	 */
	class ClientThread extends Thread{
		@Override
		public void run() {
			super.run();
			
			try {
				mSocket = new Socket(ModemConfig.serverIp, ModemConfig.port);
				
				sendResult(MainActivity.MSG_HANDLER_CONNECT, "连接成功！", false, true, false);
				
				isConnected = true;
				
				inputStream = mSocket.getInputStream();
				outputStream = mSocket.getOutputStream();
				
				reader = new BufferedReader(new InputStreamReader(inputStream));
				writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));
				
			} catch (UnknownHostException e) {
				sendResult(MainActivity.MSG_HANDLER_CONNECT, "未知主机，无法连接IP", true, false,  true);
				e.printStackTrace();
			} catch (IOException e) {
				sendResult(MainActivity.MSG_HANDLER_CONNECT, "IO异常，无法连接IP", true, false, true);
				e.printStackTrace();
			}
			
			while(isConnected){
				try {
					String cmdStr = reader.readLine();
					
					if(!TextUtils.isEmpty(cmdStr)){
						sendResult(MainActivity.MSG_HANDLER_CONNECT, "receive msg from server " + mSocket.getRemoteSocketAddress().toString() + ":" + cmdStr, false, false, false);
						
						BaseCommand command = CommandHandler.packageReceivedCmd(cmdStr);
//						executeReceivedCmd(command);
						String cmd = command.getCmd();
						String ip = command.getIp();
						String macAddr = command.getMacAddr();
						String authenMsg = command.getAuthenMsg();
						String stateMsg = command.getStateMsg();
						
						//确定是对应的设备
						if(ip.equals(ModemConfig.localIP) && macAddr.equals(ModemConfig.localMacAddr) && authenMsg.equals(ModemConfig.authenMsg)){
							sendResult(MainActivity.MSG_HANDLER_CONNECT, "right device", false, false, false);
							
							if(cmd.equals(CMDS.AUTHEN_SUCCESS)){
								sendResultResponse(MainActivity.MSG_HANDLER_AUTHEN, "设备认证成功！" + stateMsg, true);
								isAuthened = true;
								
							}else if(cmd.equals(CMDS.AUTHEN_FAILED)){
								sendResultResponse(MainActivity.MSG_HANDLER_AUTHEN, "设备认证失败！" + stateMsg, false);
								isAuthened = false;
								
							}else if(cmd.equals(CMDS.CALL_SUCCESS)){
								sendResultResponse(MainActivity.MSG_HANDLER_CALL, "通话申请成功！" + stateMsg, true);
								isCalling = true;
								//发起通话流程
								
								
							}else if(cmd.equals(CMDS.CALL_FAILED)){
								sendResultResponse(MainActivity.MSG_HANDLER_CALL, "通话申请失败！" + stateMsg, false);
								isCalling = false;
								
							}else {
								sendResultResponse(MainActivity.MSG_HANDLER_CONNECT, "未知错误！" + stateMsg, false);
							}
							
						}else {
							sendResultResponse(MainActivity.MSG_HANDLER_CONNECT, "收到非法信息:" + cmdStr, false);
						}
						
					}
					
				} catch (IOException e) {
					sendResult(MainActivity.MSG_HANDLER_CONNECT, "命令无法读取！", true, false, false);
					e.printStackTrace();
				}
				
				try {
					new Thread().sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	public void disConnect(){
		if(mSocket != null && mSocket.isConnected()){
			try {
				mSocket.close();
				mSocket = null;
				isConnected = false;
				isAuthened = false;
			} catch (IOException e) {
				sendResult(MainActivity.MSG_HANDLER_CONNECT, "断开异常", true, false, true);
				e.printStackTrace();
			}
		}
		
	}
	
	public void applyAuthen(){
		if(isConnect()){
			BaseCommand command = new BaseCommand(CMDS.APPLY_FOR_AUTHEN, ModemConfig.localIP, ModemConfig.localMacAddr, ModemConfig.authenMsg, "");
			write(command);
			sendResult(MainActivity.MSG_HANDLER_AUTHEN, "发送认证申请 " + command.toString(), false, false, true);
			
		}else {
			sendResult(MainActivity.MSG_HANDLER_AUTHEN, "请先连接到服务器！", true, false, true);
		}
	}
	
	public void applyCall(){
		if(isConnect()){
			if(isAuthened()){
				BaseCommand command = new BaseCommand(CMDS.APPLY_FOR_CALL, ModemConfig.localIP, ModemConfig.localMacAddr, ModemConfig.authenMsg, "");
				write(command);
				sendResult(MainActivity.MSG_HANDLER_CALL, "发送通话申请 " + command.toString(), false, false, true);
			}else {
				sendResult(MainActivity.MSG_HANDLER_CALL, "请先向服务器认证！", true, false, true);
			}
		}else {
			sendResult(MainActivity.MSG_HANDLER_CALL, "请先连接到服务器！", true, false, true);
		}
	}
	
	public boolean isConnect(){
		
		return isConnected;
	}
	
	public boolean isAuthened(){
		
		return isAuthened;
	}
	
	public boolean isCalling(){
		
		return isCalling;
	}
	
	public void write(BaseCommand cmd){
//		connectedDeviceInfo.addCmdHistory(cmd.toString());
		writer.write(cmd.toString());
		sendResultResponse(MainActivity.MSG_HANDLER_CONNECT, "写入消息" + cmd.toString(), false);
	}
	
	public void write(String cmd, String ip, String macAddr, String authenMsg, String stateMsg){
		BaseCommand command = new BaseCommand(cmd, ip, macAddr, authenMsg, stateMsg);
		write(command);
	}
	
	/**
	 * 发送状态结果
	 * @param what 任务种类
	 * @param msg 任务详情
	 * @param isException 是否是异常信息
	 * @param isSuccess 执行是否成功
	 * @param isRequest 是请求还是响应
	 */
	private void sendResult(int what, String msg, boolean isException, boolean isSuccess, boolean isRequest){
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString(KEY_BUNDLE_MSG, msg);
		bundle.putBoolean(KEY_BUNDLE_ISEXCEPTION, isException);
		bundle.putBoolean(KEY_BUNDLE_ISSUCCESS, isSuccess);
		bundle.putBoolean(KEY_BUNDLE_ISREQUEST, isRequest);
		message.what = what;
		message.setData(bundle);
		mHandler.sendMessage(message);
	}
	
	/**
	 * 服务器反馈
	 * @param what
	 * @param msg
	 * @param isSuccess 是否成功
	 */
	private void sendResultResponse(int what, String msg, boolean isSuccess){
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString(KEY_BUNDLE_MSG, msg);
		bundle.putBoolean(KEY_BUNDLE_ISEXCEPTION, false);
		bundle.putBoolean(KEY_BUNDLE_ISSUCCESS, isSuccess);
		bundle.putBoolean(KEY_BUNDLE_ISREQUEST, false);
		message.what = what;
		message.setData(bundle);
		mHandler.sendMessage(message);
	}
	
}