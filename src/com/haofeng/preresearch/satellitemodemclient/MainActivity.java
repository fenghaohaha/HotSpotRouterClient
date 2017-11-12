package com.haofeng.preresearch.satellitemodemclient;

import com.haofeng.preresearch.satellitemodemclient.config.ModemConfig;
import com.haofeng.preresearch.satellitemodemclient.server.SatelliteModemClientServer;
import com.haofeng.preresearch.satellitemodemclient.utils.ModemUtils;
import com.haofeng.preresearch.satellitemodemclient.utils.TimeUtils;
import com.haofeng.preresearch.shotspotsetting.server.command.StateMsg;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText mEtIp;
	private EditText mEtPort;
	private Button mBtConnectServer;
	private Button mBtAuthen;
	private Button mBtCall;
	private TextView mTvLog;

	private SatelliteModemClientServer mServer;
	private ModemConfig modemConfig;

	public final static int MSG_HANDLER_AUTHEN = 0x0011;
	public final static int MSG_HANDLER_CONNECT = 0x0012;
	public final static int MSG_HANDLER_CALL = 0x0013;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
		initListener();

		mServer = mServer.getInstance(MainActivity.this, mHandler);
		modemConfig = new ModemConfig(MainActivity.this);

	}

	private void initView() {
		mEtIp = (EditText) findViewById(R.id.et_ip);
		mEtPort = (EditText) findViewById(R.id.et_port);
		mBtConnectServer = (Button) findViewById(R.id.bt_connect_server);
		mBtAuthen = (Button) findViewById(R.id.bt_apply_authen);
		mBtCall = (Button) findViewById(R.id.bt_apply_call);
		mTvLog = (TextView) findViewById(R.id.tv_state_msg);

		mEtIp.setText(modemConfig.serverIp);
		mEtPort.setText(modemConfig.port + "");
	}

	private void initListener() {
		mBtConnectServer.setOnClickListener(clickListener);
		mBtAuthen.setOnClickListener(clickListener);
		mBtCall.setOnClickListener(clickListener);
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			String ip = mEtIp.getText().toString().trim();
			String portStr = mEtPort.getText().toString().trim();

			if (!ModemUtils.vertifyIp(ip) || TextUtils.isEmpty(ip) || TextUtils.isEmpty(portStr)) {

				showToast("请先填写正确的服务器地址和端口号！");
				return;
			}

			int port = Integer.parseInt(portStr);
			
			modemConfig.serverIp = ip;
			modemConfig.port = port;
			modemConfig.recordConfig();

			switch (v.getId()) {
			case R.id.bt_apply_authen: {
				mServer.applyAuthen();
			}
				break;
			case R.id.bt_apply_call: {
				mServer.applyCall();
			}
				break;
			case R.id.bt_connect_server: {
				mServer.initConnect();
			}
				break;
			default:
				break;
			}
		}
	};

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			Bundle bundle = msg.getData();
			boolean isException = bundle.getBoolean(SatelliteModemClientServer.KEY_BUNDLE_ISEXCEPTION);
			
			boolean isSuccess = bundle.getBoolean(SatelliteModemClientServer.KEY_BUNDLE_ISSUCCESS);
			boolean isRequest = bundle.getBoolean(SatelliteModemClientServer.KEY_BUNDLE_ISREQUEST);
			
			String result = bundle.getString(SatelliteModemClientServer.KEY_BUNDLE_MSG);
			
			appendLog(result, isException);
			
			switch (msg.what) {
			case MSG_HANDLER_AUTHEN: {
				if(isSuccess && !isException){
					mBtAuthen.setText("已成功认证服务器");
					mBtAuthen.setEnabled(false);
				}else {
					mBtAuthen.setText("请求认证");
					mBtAuthen.setEnabled(true);
				}
			}
				break;
			case MSG_HANDLER_CALL: {
				if(isSuccess && !isException){
					mBtCall.setText("可以请求通话！");
				}else {
					mBtCall.setText("请求通话");
				}
			}
				break;
			case MSG_HANDLER_CONNECT: {
				if(isSuccess && !isException){
					mBtConnectServer.setText("已连接到服务器");
					mBtConnectServer.setEnabled(false);
					
					mEtIp.setEnabled(false);
					mEtPort.setEnabled(false);
					
				}else {
					mBtConnectServer.setText("连接服务器");
					mBtConnectServer.setEnabled(true);
					
					mEtIp.setEnabled(true);
					mEtPort.setEnabled(true);
					
				}
			}
				break;

			default:
				break;
			}
		};
	};

	private void showToast(String msg) {
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 添加日志信息
	 * 
	 * @param msg
	 */
	private void appendLog(String msg , boolean isException) {
		String state = "";
		if(isException){
			state = TimeUtils.getSystemTime() + ":" + "(错误)" + msg + "\n";
		}else {
			state = TimeUtils.getSystemTime() + ":" + msg + "\n";
		}
		mTvLog.append(state);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mServer.disConnect();
	}

}
